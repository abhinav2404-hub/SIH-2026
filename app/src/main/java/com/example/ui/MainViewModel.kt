package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiLegalMetrologyService
import com.example.data.api.GroundedHelpResult
import com.example.data.auth.AuthManager
import com.example.data.auth.OfficerRole
import com.example.data.auth.OfficerUser
import com.example.data.auth.PhoneNumberAuthCredential
import com.example.data.db.AppDatabase
import com.example.data.engine.AuditReportGenerator
import com.example.data.engine.RulesetComplianceEngine
import com.example.data.locale.AppLanguage
import com.example.data.locale.AppStrings
import com.example.data.model.GlobalComplianceResult
import com.example.data.model.InspectionRecord
import com.example.data.model.MetrologyRuleEntity
import com.example.data.model.RuleCheckResult
import com.example.data.model.SamplePackage
import com.example.data.model.SyncQueueEntity
import com.example.data.di.DatabaseModule
import com.example.data.local.ProductEntity
import com.example.data.local.ScanEntity
import com.example.data.repository.InspectionRepository
import com.example.data.repository.MetrologyRuleRepository
import com.example.data.ruleset.RegulatoryRuleset
import com.example.data.ruleset.RulesetRegistry
import com.example.data.scanner.ComplianceEngine
import com.example.data.scanner.SamplePackagesRepository
import com.example.data.sync.NetworkConnectivityObserver
import com.example.data.sync.SyncManager
import com.example.util.HapticFeedbackHelper
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    LOGIN,
    OTP_VERIFY,
    ONBOARDING,
    DASHBOARD,
    SCANNER,
    CAMERA,
    ANALYSIS_RESULT,
    HISTORY,
    NOTICE_VIEW,
    TOOLS,
    RULE_GUIDE,
    HELP_SEARCH,
    FOOD_TRANSPARENCY,
    OFFLINE_QUEUE,
    SYSTEM_HEALTH,
    SYNTHETIC_TESTS,
    SETTINGS
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val authManager = AuthManager()
    private val database = AppDatabase.getDatabase(application)
    private val repository = InspectionRepository(database.inspectionDao())
    val ruleRepository = MetrologyRuleRepository(database.metrologyRuleDao())
    private val syncQueueDao = database.syncQueueDao()
    private val scanRepository = DatabaseModule.provideScanRepository(application)
    private val syncRepository = DatabaseModule.provideSyncRepository(application)
    private val geminiService = GeminiLegalMetrologyService()

    val currentUser: StateFlow<OfficerUser?> = authManager.currentUser
    val isLoggedIn: StateFlow<Boolean> = authManager.isLoggedIn

    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Active Regulatory Ruleset (India PCR 2011 default, dynamic globally)
    private val _activeRuleset = MutableStateFlow(RulesetRegistry.INDIA_PCR_2011)
    val activeRuleset: StateFlow<RegulatoryRuleset> = _activeRuleset.asStateFlow()

    // Offline Mode Toggle (For Airplane Mode testing & Demo)
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    init {
        viewModelScope.launch {
            NetworkConnectivityObserver(application).isConnectedFlow.collect { isConnected ->
                if (isConnected && !_isOfflineMode.value) {
                    syncRepository.syncPendingScans()
                    SyncManager.triggerImmediateSync(getApplication())
                }
            }
        }
    }

    // Offline Sync Queue Stream
    val offlineQueueCount: StateFlow<Int> = syncQueueDao.getPendingCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val offlineQueueItems: StateFlow<List<SyncQueueEntity>> = syncQueueDao.getAllQueueItemsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Standardized Global Compliance Output
    private val _latestGlobalResult = MutableStateFlow<GlobalComplianceResult?>(null)
    val latestGlobalResult: StateFlow<GlobalComplianceResult?> = _latestGlobalResult.asStateFlow()

    // Localization & Theme
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.LIGHT)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    // Auth State
    private val _loginPhone = MutableStateFlow("9876543210")
    val loginPhone: StateFlow<String> = _loginPhone.asStateFlow()

    private val _loginOfficerName = MutableStateFlow("Inspector R. K. Verma")
    val loginOfficerName: StateFlow<String> = _loginOfficerName.asStateFlow()

    private val _loginRole = MutableStateFlow(OfficerRole.SENIOR_LMO)
    val loginRole: StateFlow<OfficerRole> = _loginRole.asStateFlow()

    private val _enteredOtp = MutableStateFlow("")
    val enteredOtp: StateFlow<String> = _enteredOtp.asStateFlow()

    private val _latestGeneratedOtp = MutableStateFlow("123456")
    val latestGeneratedOtp: StateFlow<String> = _latestGeneratedOtp.asStateFlow()

    private val _showSmsBanner = MutableStateFlow(false)
    val showSmsBanner: StateFlow<Boolean> = _showSmsBanner.asStateFlow()

    private val _otpStatusMessage = MutableStateFlow<String?>(null)
    val otpStatusMessage: StateFlow<String?> = _otpStatusMessage.asStateFlow()

    private val _isResending = MutableStateFlow(false)
    val isResending: StateFlow<Boolean> = _isResending.asStateFlow()

    private val _otpError = MutableStateFlow<String?>(null)
    val otpError: StateFlow<String?> = _otpError.asStateFlow()

    private val _verificationId = MutableStateFlow("ver_mudra_check_token")
    val verificationId: StateFlow<String> = _verificationId.asStateFlow()

    private val _resendTimer = MutableStateFlow(45)
    val resendTimer: StateFlow<Int> = _resendTimer.asStateFlow()

    private var countdownJob: Job? = null

    // Scanner & CameraX
    private val _isCameraOpen = MutableStateFlow(false)
    val isCameraOpen: StateFlow<Boolean> = _isCameraOpen.asStateFlow()

    private val _selectedSample = MutableStateFlow<SamplePackage?>(null)
    val selectedSample: StateFlow<SamplePackage?> = _selectedSample.asStateFlow()

    private val _currentInspectionRecord = MutableStateFlow<InspectionRecord?>(null)
    val currentInspectionRecord: StateFlow<InspectionRecord?> = _currentInspectionRecord.asStateFlow()

    private val _currentRuleResults = MutableStateFlow<List<RuleCheckResult>>(emptyList())
    val currentRuleResults: StateFlow<List<RuleCheckResult>> = _currentRuleResults.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisStep = MutableStateFlow("")
    val analysisStep: StateFlow<String> = _analysisStep.asStateFlow()

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap: StateFlow<Bitmap?> = _capturedBitmap.asStateFlow()

    // History, Search & Categorization
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("ALL")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _categoryFilter = MutableStateFlow("ALL")
    val categoryFilter: StateFlow<String> = _categoryFilter.asStateFlow()

    val inspectionHistory: StateFlow<List<InspectionRecord>> = combine(
        repository.allInspections,
        _searchQuery,
        _statusFilter,
        _categoryFilter
    ) { list, query, status, category ->
        list.sortedByDescending { it.timestamp }.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.productName.contains(query, ignoreCase = true) ||
                    item.brandName.contains(query, ignoreCase = true) ||
                    item.barcode.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true) ||
                    item.inspectorName.contains(query, ignoreCase = true) ||
                    item.inspectionLocation.contains(query, ignoreCase = true) ||
                    item.batchLotNumber.contains(query, ignoreCase = true) ||
                    item.manufacturerAddress.contains(query, ignoreCase = true)

            val matchesStatus = when (status) {
                "COMPLIANT" -> item.overallStatus == "COMPLIANT"
                "NON_COMPLIANT" -> item.overallStatus == "NON_COMPLIANT" || item.overallStatus == "MINOR_VIOLATIONS"
                "SEIZURE" -> item.overallStatus == "SEIZURE_RECOMMENDED"
                else -> true
            }

            val matchesCategory = when (category) {
                "AGRI" -> item.category.contains("Seed", ignoreCase = true) ||
                        item.category.contains("Fertiliz", ignoreCase = true) ||
                        item.category.contains("Pesticid", ignoreCase = true) ||
                        item.category.contains("Crop", ignoreCase = true) ||
                        item.category.contains("Agri", ignoreCase = true) ||
                        item.category.contains("Soil", ignoreCase = true) ||
                        item.category.contains("Farm", ignoreCase = true)
                "OILS" -> item.category.contains("Oil", ignoreCase = true) ||
                        item.category.contains("Ghee", ignoreCase = true) ||
                        item.category.contains("Butter", ignoreCase = true) ||
                        item.category.contains("Fat", ignoreCase = true) ||
                        item.category.contains("Dairy", ignoreCase = true)
                "FOOD" -> item.category.contains("Food", ignoreCase = true) ||
                        item.category.contains("Snack", ignoreCase = true) ||
                        item.category.contains("Beverage", ignoreCase = true) ||
                        item.category.contains("Grain", ignoreCase = true) ||
                        item.category.contains("Pulse", ignoreCase = true) ||
                        item.category.contains("Flour", ignoreCase = true) ||
                        item.category.contains("Packag", ignoreCase = true) ||
                        item.category.contains("Spice", ignoreCase = true)
                "ECO" -> item.category.contains("Eco", ignoreCase = true) ||
                        item.category.contains("Environ", ignoreCase = true) ||
                        item.category.contains("Bio", ignoreCase = true) ||
                        item.category.contains("Organ", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesStatus && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room Database Rules Stream
    val allMetrologyRules: StateFlow<List<MetrologyRuleEntity>> = ruleRepository.allRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculators
    val uspMrp = MutableStateFlow("150.00")
    val uspQuantity = MutableStateFlow("500")
    val uspUnit = MutableStateFlow("g")

    val fontPackArea = MutableStateFlow("120")
    val fontNetWeight = MutableStateFlow("400")

    // Google Search Grounded Knowledge Help
    private val _helpSearchQuery = MutableStateFlow("")
    val helpSearchQuery: StateFlow<String> = _helpSearchQuery.asStateFlow()

    private val _helpSelectedCategory = MutableStateFlow("ALL")
    val helpSelectedCategory: StateFlow<String> = _helpSelectedCategory.asStateFlow()

    private val _helpResult = MutableStateFlow<GroundedHelpResult?>(null)
    val helpResult: StateFlow<GroundedHelpResult?> = _helpResult.asStateFlow()

    private val _isSearchingHelp = MutableStateFlow(false)
    val isSearchingHelp: StateFlow<Boolean> = _isSearchingHelp.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedSampleRecordsIfNeeded()
            ruleRepository.seedDefaultRulesIfNeeded()
            // Initial seed for grounded reference
            performGroundedHelpSearch("Legal Metrology Packaged Commodities Rules 2011 mandatory declarations summary", "LEGAL_METROLOGY")
        }
    }

    // Language & Theme Management
    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun toggleTheme() {
        _themeMode.value = if (_themeMode.value == ThemeMode.LIGHT) ThemeMode.DARK else ThemeMode.LIGHT
    }

    fun getString(key: String): String = AppStrings.get(key, _currentLanguage.value)

    // Navigation
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // Auth Actions
    fun setLoginPhone(phone: String) {
        _loginPhone.value = phone.filter { it.isDigit() }.take(10)
        _otpError.value = null
    }

    fun setLoginOfficerName(name: String) {
        _loginOfficerName.value = name
    }

    fun setLoginRole(role: OfficerRole) {
        _loginRole.value = role
        // Prepopulate standard test representative names per role
        when (role) {
            OfficerRole.SENIOR_LMO -> _loginOfficerName.value = "Inspector R. K. Verma"
            OfficerRole.AGRI_PACKAGER -> _loginOfficerName.value = "KrishiVeda Agro Quality Lead"
            OfficerRole.FARMER_FPO -> _loginOfficerName.value = "Kisan Utthan FPO (Bhiwani)"
            OfficerRole.GRIEVANCE_AUDITOR -> _loginOfficerName.value = "Suresh Kumar (Citizen Auditor)"
            OfficerRole.STATE_CONTROLLER -> _loginOfficerName.value = "Dr. S. K. Joshi (State Controller)"
        }
    }

    fun setEnteredOtp(otp: String) {
        val sanitized = otp.filter { it.isDigit() }.take(6)
        _enteredOtp.value = sanitized
        _otpError.value = null
        // Early execution: User intent is completely clear once 6th digit is entered
        if (sanitized.length == 6) {
            verifyOtp()
        }
    }

    fun requestOtp() {
        if (_loginPhone.value.length < 10) {
            _otpError.value = "Please enter a valid 10-digit mobile number."
            return
        }
        val (verId, generatedOtp) = authManager.sendOtp(_loginPhone.value, _loginOfficerName.value, _loginRole.value)
        _verificationId.value = verId
        _latestGeneratedOtp.value = generatedOtp
        _enteredOtp.value = ""
        _otpError.value = null
        _otpStatusMessage.value = "One-Time Password sent to +91 ${_loginPhone.value}"
        _showSmsBanner.value = true
        HapticFeedbackHelper.vibrateClick(getApplication())
        startOtpCountdown()
        _currentScreen.value = AppScreen.OTP_VERIFY
    }

    fun resendOtp() {
        if (_resendTimer.value > 0) return
        _isResending.value = true
        viewModelScope.launch {
            // Immediate dispatch with zero artificial delay
            val (verId, newOtp) = authManager.resendOtp()
            _verificationId.value = verId
            _latestGeneratedOtp.value = newOtp
            _enteredOtp.value = ""
            _otpError.value = null
            _otpStatusMessage.value = "New OTP resent successfully to +91 ${_loginPhone.value}"
            _showSmsBanner.value = true
            _isResending.value = false
            HapticFeedbackHelper.vibrateCaptureSuccess(getApplication())
            startOtpCountdown()
        }
    }

    fun dismissSmsBanner() {
        _showSmsBanner.value = false
    }

    fun autoFillGeneratedOtp() {
        val otp = _latestGeneratedOtp.value.ifBlank { "123456" }
        _enteredOtp.value = otp
        _otpError.value = null
        HapticFeedbackHelper.vibrateClick(getApplication())
        verifyOtp()
    }

    private fun startOtpCountdown() {
        countdownJob?.cancel()
        _resendTimer.value = 30
        countdownJob = viewModelScope.launch {
            while (_resendTimer.value > 0) {
                delay(1000)
                _resendTimer.value = _resendTimer.value - 1
            }
        }
    }

    fun verifyOtp() {
        val otpToVerify = _enteredOtp.value.ifBlank { _latestGeneratedOtp.value.ifBlank { "123456" } }
        val credential = PhoneNumberAuthCredential(
            verificationId = _verificationId.value,
            smsCode = otpToVerify,
            phoneNumber = _loginPhone.value
        )
        val success = authManager.verifyWithCredential(credential)
        if (success) {
            _otpError.value = null
            _showSmsBanner.value = false
            HapticFeedbackHelper.vibrateAiProcessingComplete(getApplication())
            _currentScreen.value = AppScreen.DASHBOARD
        } else {
            _otpError.value = "Invalid OTP code. Please enter the 6-digit code received or use ${_latestGeneratedOtp.value}."
        }
    }

    fun autoFillDemoOtp() {
        val otp = _latestGeneratedOtp.value.ifBlank { "123456" }
        _enteredOtp.value = otp
        verifyOtp()
    }

    fun logout() {
        authManager.logout()
        _currentScreen.value = AppScreen.LOGIN
    }

    // Camera Controls
    fun openCamera() {
        _isCameraOpen.value = true
    }

    fun closeCamera() {
        _isCameraOpen.value = false
    }

    fun onCameraPhotoCaptured(bitmap: Bitmap) {
        _isCameraOpen.value = false
        analyzeCapturedBitmap(bitmap)
    }

    // Ruleset Switching
    fun setRuleset(rulesetId: String) {
        val ruleset = RulesetRegistry.getRuleset(rulesetId)
        _activeRuleset.value = ruleset
    }

    fun setActiveRuleset(rulesetId: String) {
        setRuleset(rulesetId)
    }

    fun setOfficerRole(role: OfficerRole) {
        setLoginRole(role)
    }

    // Toggle Offline Mode
    fun toggleOfflineMode() {
        setOfflineMode(!_isOfflineMode.value)
    }

    fun setOfflineMode(enabled: Boolean) {
        _isOfflineMode.value = enabled
        if (!enabled) {
            triggerBackgroundSync()
        }
    }

    // WorkManager Sync Trigger
    fun triggerBackgroundSync() {
        viewModelScope.launch {
            SyncManager.triggerImmediateSync(getApplication())
            syncRepository.syncPendingScans()
        }
    }

    fun clearSyncedQueue() {
        viewModelScope.launch {
            syncQueueDao.clearSynced()
        }
    }

    // Generate Audit Reports
    fun getAuditReportJson(): String {
        val current = _latestGlobalResult.value
        return if (current != null) {
            AuditReportGenerator.generateJsonReport(current)
        } else {
            "{ \"error\": \"No active inspection available for audit export.\" }"
        }
    }

    fun getAuditReportText(): String {
        val current = _latestGlobalResult.value ?: return "No inspection record available."
        val officer = currentUser.value
        return AuditReportGenerator.generateTextSummaryReport(
            result = current,
            inspectorName = officer?.name ?: "Legal Metrology Officer",
            inspectorBadge = officer?.officerId ?: "LMO-DL-2026-0842",
            location = officer?.jurisdiction ?: "Central Enforcement Wing"
        )
    }

    // Scanning & Compliance Evaluation
    fun selectSamplePackage(sample: SamplePackage) {
        _selectedSample.value = sample
        _capturedBitmap.value = null
        runSampleAnalysis(sample)
    }

    fun runSampleAnalysis(sample: SamplePackage) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _currentScreen.value = AppScreen.SCANNER
            val activeRule = _activeRuleset.value
            _analysisStep.value = "Evaluating statutory compliance (${activeRule.rulesetId})..."

            val officer = currentUser.value
            val inspectorName = officer?.name ?: "Inspector R. K. Verma"
            val inspectorBadge = officer?.officerId ?: "LMO-DL-2026-0842"
            val location = officer?.jurisdiction ?: "Central Agricultural & Metrology Wing"

            val (record, rules) = ComplianceEngine.analyzeSamplePackage(
                sample = sample,
                inspectorName = inspectorName,
                inspectorBadge = inspectorBadge,
                location = location
            )

            // Evaluate dynamically via RulesetComplianceEngine
            val globalResult = RulesetComplianceEngine.evaluate(
                scanId = record.sampleId ?: UUID.randomUUID().toString(),
                rulesetId = activeRule.rulesetId,
                productName = sample.title,
                brand = sample.brand,
                manufacturer = sample.manufacturer,
                importer = "",
                mrpValue = sample.declaredMrp.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 100.0,
                currency = activeRule.currency,
                isInclusiveTaxes = true,
                netQtyValue = sample.netQuantity.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 500.0,
                netQtyUnit = if (sample.netQuantity.lowercase().contains("kg")) "kg" else if (sample.netQuantity.lowercase().contains("l")) "L" else "g",
                mfgDate = sample.mfgDate,
                expiryDate = sample.expiryDate,
                bestBefore = sample.expiryDate,
                batchNumber = sample.batchNo,
                ingredients = listOf(sample.ingredients),
                allergens = listOf(sample.allergens),
                countryOfOrigin = sample.countryOfOrigin,
                customerCare = sample.consumerCare,
                measuredFontHeightMm = sample.fontHeightMm,
                barcode = sample.id,
                qrData = sample.qrCodeData,
                rawOcrText = "${sample.title} MRP ${sample.declaredMrp} Net Qty: ${sample.netQuantity} USP: ${sample.declaredUsp} Mfd by: ${sample.manufacturer} Batch: ${sample.batchNo}",
                captureMode = if (_isOfflineMode.value) "QUEUED_OFFLINE" else "ONLINE",
                syncStatus = if (_isOfflineMode.value) "PENDING" else "SYNCED",
                locale = activeRule.languages.firstOrNull() ?: "en-IN"
            )

            _latestGlobalResult.value = globalResult

            val savedId = repository.insertInspection(record)
            _currentInspectionRecord.value = record.copy(id = savedId)
            _currentRuleResults.value = rules

            val scanEntity = ScanEntity(
                scanId = globalResult.scanId,
                productId = record.barcode.ifBlank { UUID.randomUUID().toString() },
                timestamp = record.timestamp,
                rulesetId = activeRule.rulesetId,
                overallStatus = record.overallStatus,
                complianceScore = record.complianceScore,
                rawOcrText = record.rawFullOcrText,
                inspectorBadge = record.inspectorBadge,
                inspectorNotes = record.officerNotes,
                syncStatus = if (_isOfflineMode.value) "PENDING" else "SYNCED",
                violationsCount = record.violationsCount
            )
            val productEntity = ProductEntity(
                productId = scanEntity.productId ?: UUID.randomUUID().toString(),
                barcode = record.barcode,
                brand = record.brandName,
                name = record.productName,
                category = record.category,
                mrpDeclared = record.declaredMrp.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0,
                netQuantityDeclared = record.netQuantity
            )
            scanRepository.saveScanTransaction(scanEntity, productEntity, emptyList())

            // Handle offline queue if offline mode active
            if (_isOfflineMode.value) {
                syncQueueDao.insert(
                    SyncQueueEntity(
                        scanId = globalResult.scanId,
                        rulesetId = activeRule.rulesetId,
                        payloadJson = AuditReportGenerator.generateJsonReport(globalResult),
                        status = "PENDING"
                    )
                )
            } else {
                syncRepository.syncPendingScans()
            }

            _isAnalyzing.value = false
            HapticFeedbackHelper.vibrateAiProcessingComplete(getApplication())
            _currentScreen.value = AppScreen.ANALYSIS_RESULT
        }
    }

    fun analyzeCapturedBitmap(bitmap: Bitmap) {
        _capturedBitmap.value = bitmap
        _selectedSample.value = null
        viewModelScope.launch {
            _isAnalyzing.value = true
            _currentScreen.value = AppScreen.SCANNER
            val activeRule = _activeRuleset.value
            _analysisStep.value = "Multimodal AI parsing mandatory declarations against ${activeRule.rulesetId}..."

            val officer = currentUser.value
            val inspectorName = officer?.name ?: "Inspector R. K. Verma"
            val inspectorBadge = officer?.officerId ?: "LMO-DL-2026-0842"
            val location = officer?.jurisdiction ?: "Central Agricultural & Metrology Wing"

            val (record, rules) = geminiService.analyzePackageImage(
                bitmap = bitmap,
                inspectorName = inspectorName,
                inspectorBadge = inspectorBadge,
                location = location
            )

            val globalResult = RulesetComplianceEngine.evaluate(
                scanId = UUID.randomUUID().toString(),
                rulesetId = activeRule.rulesetId,
                productName = record.productName,
                brand = record.brandName,
                manufacturer = record.manufacturerAddress,
                importer = "",
                mrpValue = record.declaredMrp.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 185.0,
                currency = activeRule.currency,
                isInclusiveTaxes = true,
                netQtyValue = record.netQuantity.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 1000.0,
                netQtyUnit = if (record.netQuantity.lowercase().contains("kg")) "kg" else if (record.netQuantity.lowercase().contains("l")) "L" else "g",
                mfgDate = record.mfgPackingDate,
                expiryDate = record.expiryDate,
                bestBefore = record.expiryDate,
                batchNumber = record.batchLotNumber,
                ingredients = listOf(record.ingredientsList),
                allergens = listOf(record.allergens),
                countryOfOrigin = record.countryOfOrigin,
                customerCare = record.consumerCareContact,
                measuredFontHeightMm = 3.5,
                barcode = record.barcode,
                qrData = record.qrCodeData,
                rawOcrText = record.rawFullOcrText,
                captureMode = if (_isOfflineMode.value) "QUEUED_OFFLINE" else "ONLINE",
                syncStatus = if (_isOfflineMode.value) "PENDING" else "SYNCED",
                locale = activeRule.languages.firstOrNull() ?: "en-IN"
            )

            _latestGlobalResult.value = globalResult

            val savedId = repository.insertInspection(record)
            _currentInspectionRecord.value = record.copy(id = savedId)
            _currentRuleResults.value = rules

            val scanEntity = ScanEntity(
                scanId = globalResult.scanId,
                productId = record.barcode.ifBlank { UUID.randomUUID().toString() },
                timestamp = record.timestamp,
                rulesetId = activeRule.rulesetId,
                overallStatus = record.overallStatus,
                complianceScore = record.complianceScore,
                rawOcrText = record.rawFullOcrText,
                inspectorBadge = record.inspectorBadge,
                inspectorNotes = record.officerNotes,
                syncStatus = if (_isOfflineMode.value) "PENDING" else "SYNCED",
                violationsCount = record.violationsCount
            )
            val productEntity = ProductEntity(
                productId = scanEntity.productId ?: UUID.randomUUID().toString(),
                barcode = record.barcode,
                brand = record.brandName,
                name = record.productName,
                category = record.category,
                mrpDeclared = record.declaredMrp.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0,
                netQuantityDeclared = record.netQuantity
            )
            scanRepository.saveScanTransaction(scanEntity, productEntity, emptyList())

            if (_isOfflineMode.value) {
                syncQueueDao.insert(
                    SyncQueueEntity(
                        scanId = globalResult.scanId,
                        rulesetId = activeRule.rulesetId,
                        payloadJson = AuditReportGenerator.generateJsonReport(globalResult),
                        status = "PENDING"
                    )
                )
            } else {
                syncRepository.syncPendingScans()
            }

            _isAnalyzing.value = false
            HapticFeedbackHelper.vibrateAiProcessingComplete(getApplication())
            _currentScreen.value = AppScreen.ANALYSIS_RESULT
        }
    }

    fun analyzeCustomLabelText(productName: String, brand: String, category: String, rawText: String) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _currentScreen.value = AppScreen.SCANNER
            _analysisStep.value = "Evaluating Rule 6 & Schedule II statutory standards..."

            val officer = currentUser.value
            val inspectorName = officer?.name ?: "Inspector R. K. Verma"
            val inspectorBadge = officer?.officerId ?: "LMO-DL-2026-0842"
            val location = officer?.jurisdiction ?: "Central Agricultural & Metrology Wing"

            val (record, rules) = ComplianceEngine.analyzeCustomText(
                productName = productName,
                brandName = brand,
                category = category,
                rawLabelText = rawText,
                inspectorName = inspectorName,
                inspectorBadge = inspectorBadge,
                location = location
            )

            val activeRule = _activeRuleset.value
            val globalResult = RulesetComplianceEngine.evaluate(
                scanId = UUID.randomUUID().toString(),
                rulesetId = activeRule.rulesetId,
                productName = record.productName,
                brand = record.brandName,
                manufacturer = record.manufacturerAddress,
                importer = "",
                mrpValue = record.declaredMrp.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 100.0,
                currency = activeRule.currency,
                isInclusiveTaxes = true,
                netQtyValue = record.netQuantity.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 500.0,
                netQtyUnit = if (record.netQuantity.lowercase().contains("kg")) "kg" else if (record.netQuantity.lowercase().contains("l")) "L" else "g",
                mfgDate = record.mfgPackingDate,
                expiryDate = record.expiryDate,
                bestBefore = record.expiryDate,
                batchNumber = record.batchLotNumber,
                ingredients = listOf(record.ingredientsList),
                allergens = listOf(record.allergens),
                countryOfOrigin = record.countryOfOrigin,
                customerCare = record.consumerCareContact,
                measuredFontHeightMm = 3.5,
                barcode = record.barcode,
                qrData = record.qrCodeData,
                rawOcrText = record.rawFullOcrText.ifBlank { rawText },
                captureMode = if (_isOfflineMode.value) "QUEUED_OFFLINE" else "ONLINE",
                syncStatus = if (_isOfflineMode.value) "PENDING" else "SYNCED",
                locale = activeRule.languages.firstOrNull() ?: "en-IN"
            )

            _latestGlobalResult.value = globalResult

            val savedId = repository.insertInspection(record)
            _currentInspectionRecord.value = record.copy(id = savedId)
            _currentRuleResults.value = rules

            val scanEntity = ScanEntity(
                scanId = globalResult.scanId,
                productId = record.barcode.ifBlank { UUID.randomUUID().toString() },
                timestamp = record.timestamp,
                rulesetId = activeRule.rulesetId,
                overallStatus = record.overallStatus,
                complianceScore = record.complianceScore,
                rawOcrText = record.rawFullOcrText.ifBlank { rawText },
                inspectorBadge = record.inspectorBadge,
                inspectorNotes = record.officerNotes,
                syncStatus = if (_isOfflineMode.value) "PENDING" else "SYNCED",
                violationsCount = record.violationsCount
            )
            val productEntity = ProductEntity(
                productId = scanEntity.productId ?: UUID.randomUUID().toString(),
                barcode = record.barcode,
                brand = record.brandName,
                name = record.productName,
                category = record.category,
                mrpDeclared = record.declaredMrp.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0,
                netQuantityDeclared = record.netQuantity
            )
            scanRepository.saveScanTransaction(scanEntity, productEntity, emptyList())

            if (_isOfflineMode.value) {
                syncQueueDao.insert(
                    SyncQueueEntity(
                        scanId = globalResult.scanId,
                        rulesetId = activeRule.rulesetId,
                        payloadJson = AuditReportGenerator.generateJsonReport(globalResult),
                        status = "PENDING"
                    )
                )
            } else {
                syncRepository.syncPendingScans()
            }
            _isAnalyzing.value = false
            HapticFeedbackHelper.vibrateAiProcessingComplete(getApplication())
            _currentScreen.value = AppScreen.ANALYSIS_RESULT
        }
    }

    fun openExistingRecord(record: InspectionRecord) {
        _currentInspectionRecord.value = record
        val activeRule = _activeRuleset.value

        val globalResult = RulesetComplianceEngine.evaluate(
            scanId = record.sampleId ?: record.id.toString(),
            rulesetId = activeRule.rulesetId,
            productName = record.productName,
            brand = record.brandName,
            manufacturer = record.manufacturerAddress,
            importer = "",
            mrpValue = record.declaredMrp.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 100.0,
            currency = activeRule.currency,
            isInclusiveTaxes = true,
            netQtyValue = record.netQuantity.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 500.0,
            netQtyUnit = if (record.netQuantity.lowercase().contains("kg")) "kg" else if (record.netQuantity.lowercase().contains("l")) "L" else "g",
            mfgDate = record.mfgPackingDate,
            expiryDate = record.expiryDate,
            bestBefore = record.expiryDate,
            batchNumber = record.batchLotNumber,
            ingredients = if (record.quidDetails.isNotBlank()) listOf(record.quidDetails) else emptyList(),
            allergens = if (record.allergens.isNotBlank()) listOf(record.allergens) else emptyList(),
            countryOfOrigin = record.countryOfOrigin,
            customerCare = record.consumerCareContact,
            measuredFontHeightMm = 3.5,
            barcode = record.barcode,
            qrData = record.qrCodeData,
            rawOcrText = "${record.productName} MRP: ${record.declaredMrp} Net Qty: ${record.netQuantity} USP: ${record.declaredUsp} Origin: ${record.countryOfOrigin} Mfd by: ${record.manufacturerAddress}",
            captureMode = "HISTORICAL_RECORD",
            syncStatus = "SYNCED",
            locale = activeRule.languages.firstOrNull() ?: "en-IN"
        )
        _latestGlobalResult.value = globalResult

        val (_, rules) = ComplianceEngine.analyzeCustomText(
            productName = record.productName,
            brandName = record.brandName,
            category = record.category,
            rawLabelText = "${record.productName} MRP: ${record.declaredMrp} Net Qty: ${record.netQuantity} USP: ${record.declaredUsp} Origin: ${record.countryOfOrigin} Mfd by: ${record.manufacturerAddress} Care: ${record.consumerCareContact}",
            inspectorName = record.inspectorName,
            inspectorBadge = record.inspectorBadge,
            location = record.inspectionLocation
        )
        _currentRuleResults.value = rules
        _currentScreen.value = AppScreen.ANALYSIS_RESULT
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _statusFilter.value = "ALL"
        _categoryFilter.value = "ALL"
    }

    fun markNoticeGenerated() {
        val current = _currentInspectionRecord.value ?: return
        viewModelScope.launch {
            val updated = current.copy(noticeGenerated = true)
            repository.updateInspection(updated)
            _currentInspectionRecord.value = updated
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteInspection(id)
            if (_currentInspectionRecord.value?.id == id) {
                _currentInspectionRecord.value = null
                _currentScreen.value = AppScreen.HISTORY
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: String) {
        _statusFilter.value = filter
    }

    fun setCategoryFilter(category: String) {
        _categoryFilter.value = category
    }

    // Google Search Grounded Reference Actions
    fun setHelpSearchQuery(query: String) {
        _helpSearchQuery.value = query
    }

    fun setHelpSelectedCategory(category: String) {
        _helpSelectedCategory.value = category
    }

    fun performGroundedHelpSearch(query: String, category: String = _helpSelectedCategory.value) {
        if (query.isBlank()) return
        _isSearchingHelp.value = true
        _helpSearchQuery.value = query
        _helpSelectedCategory.value = category
        viewModelScope.launch {
            try {
                val result = geminiService.queryLegalMetrologyGroundedHelp(query, category)
                _helpResult.value = result
                HapticFeedbackHelper.vibrateAiProcessingComplete(getApplication())
            } catch (e: Exception) {
                // Keep previous or fallback
            } finally {
                _isSearchingHelp.value = false
            }
        }
    }
}
