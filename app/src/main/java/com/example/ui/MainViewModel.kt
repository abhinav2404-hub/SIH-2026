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
import com.example.data.locale.AppLanguage
import com.example.data.locale.AppStrings
import com.example.data.model.InspectionRecord
import com.example.data.model.MetrologyRuleEntity
import com.example.data.model.RuleCheckResult
import com.example.data.model.SamplePackage
import com.example.data.repository.InspectionRepository
import com.example.data.repository.MetrologyRuleRepository
import com.example.data.scanner.ComplianceEngine
import com.example.data.scanner.SamplePackagesRepository
import com.example.util.HapticFeedbackHelper
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
    DASHBOARD,
    SCANNER,
    CAMERA,
    ANALYSIS_RESULT,
    HISTORY,
    NOTICE_VIEW,
    TOOLS,
    RULE_GUIDE,
    HELP_SEARCH
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
    private val geminiService = GeminiLegalMetrologyService()

    val currentUser: StateFlow<OfficerUser?> = authManager.currentUser
    val isLoggedIn: StateFlow<Boolean> = authManager.isLoggedIn

    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

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
        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.productName.contains(query, ignoreCase = true) ||
                    item.brandName.contains(query, ignoreCase = true) ||
                    item.barcode.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)

            val matchesStatus = when (status) {
                "COMPLIANT" -> item.overallStatus == "COMPLIANT"
                "NON_COMPLIANT" -> item.overallStatus == "NON_COMPLIANT" || item.overallStatus == "MINOR_VIOLATIONS"
                "SEIZURE" -> item.overallStatus == "SEIZURE_RECOMMENDED"
                else -> true
            }

            val matchesCategory = when (category) {
                "AGRI" -> item.category.contains("Seed", ignoreCase = true) || item.category.contains("Fertilizer", ignoreCase = true)
                "OILS" -> item.category.contains("Oil", ignoreCase = true) || item.category.contains("Ghee", ignoreCase = true)
                "FOOD" -> item.category.contains("Food", ignoreCase = true) || item.category.contains("Snack", ignoreCase = true) || item.category.contains("Beverage", ignoreCase = true)
                "ECO" -> item.category.contains("Environmental", ignoreCase = true) || item.category.contains("Farm", ignoreCase = true)
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
            _analysisStep.value = "Evaluating statutory compliance (Legal Metrology Rules, 2011)..."

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

            val savedId = repository.insertInspection(record)
            _currentInspectionRecord.value = record.copy(id = savedId)
            _currentRuleResults.value = rules
            _isAnalyzing.value = false
            HapticFeedbackHelper.vibrateAiProcessingComplete(getApplication())
            // Render the LCP verdict and score card immediately with zero delay
            _currentScreen.value = AppScreen.ANALYSIS_RESULT
        }
    }

    fun analyzeCapturedBitmap(bitmap: Bitmap) {
        _capturedBitmap.value = bitmap
        _selectedSample.value = null
        viewModelScope.launch {
            _isAnalyzing.value = true
            _currentScreen.value = AppScreen.SCANNER
            _analysisStep.value = "Multimodal AI parsing mandatory declarations against Legal Metrology Rules..."

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

            val savedId = repository.insertInspection(record)
            _currentInspectionRecord.value = record.copy(id = savedId)
            _currentRuleResults.value = rules
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

            val savedId = repository.insertInspection(record)
            _currentInspectionRecord.value = record.copy(id = savedId)
            _currentRuleResults.value = rules
            _isAnalyzing.value = false
            HapticFeedbackHelper.vibrateAiProcessingComplete(getApplication())
            _currentScreen.value = AppScreen.ANALYSIS_RESULT
        }
    }

    fun openExistingRecord(record: InspectionRecord) {
        _currentInspectionRecord.value = record
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
