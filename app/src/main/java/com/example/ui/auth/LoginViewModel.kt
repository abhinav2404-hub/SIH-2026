package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.auth.OfficerRole
import com.example.data.auth.PhoneNumberAuthCredential
import com.example.data.locale.AppLanguage
import com.example.data.locale.AppStrings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authManager: AuthManager = AuthManager()
) : ViewModel() {

    private val _phoneNumber = MutableStateFlow("9876543210")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _userName = MutableStateFlow("Rajesh Sharma")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _selectedRole = MutableStateFlow(OfficerRole.SENIOR_LMO)
    val selectedRole: StateFlow<OfficerRole> = _selectedRole.asStateFlow()

    private val _enteredOtp = MutableStateFlow("")
    val enteredOtp: StateFlow<String> = _enteredOtp.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>("ver_demo_id")
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

    private val _latestGeneratedOtp = MutableStateFlow("123456")
    val latestGeneratedOtp: StateFlow<String> = _latestGeneratedOtp.asStateFlow()

    private val _isOtpSent = MutableStateFlow(false)
    val isOtpSent: StateFlow<Boolean> = _isOtpSent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _resendCountdown = MutableStateFlow(45)
    val resendCountdown: StateFlow<Int> = _resendCountdown.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private var timerJob: Job? = null

    fun onPhoneNumberChange(input: String) {
        val sanitized = input.filter { it.isDigit() }.take(10)
        _phoneNumber.value = sanitized
        _errorMessage.value = null
    }

    fun onUserNameChange(input: String) {
        _userName.value = input
    }

    fun onRoleSelect(role: OfficerRole) {
        _selectedRole.value = role
        // Prepopulate default representative names per portal
        when (role) {
            OfficerRole.SENIOR_LMO -> if (_userName.value.isBlank()) _userName.value = "Rajesh Sharma (Senior LMO)"
            OfficerRole.AGRI_PACKAGER -> if (_userName.value.isBlank()) _userName.value = "KrishiVeda Agrotech Quality Lead"
            OfficerRole.FARMER_FPO -> if (_userName.value.isBlank()) _userName.value = "Kisan Vikas Sangh (FPO Secretary)"
            OfficerRole.GRIEVANCE_AUDITOR -> if (_userName.value.isBlank()) _userName.value = "Amit Kumar (Citizen Auditor)"
            OfficerRole.STATE_CONTROLLER -> if (_userName.value.isBlank()) _userName.value = "Dr. S. K. Joshi (Controller Legal Metrology)"
        }
    }

    fun onOtpChange(otp: String, onSuccess: () -> Unit = {}) {
        val sanitized = otp.filter { it.isDigit() }.take(6)
        _enteredOtp.value = sanitized
        _errorMessage.value = null
        // Early execution: Once 6 digits are reached, verify automatically without waiting for explicit confirm click
        if (sanitized.length == 6) {
            verifyOtp(onSuccess)
        }
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun requestOtp(onSuccess: () -> Unit = {}) {
        val phone = _phoneNumber.value.trim()
        if (phone.length < 10) {
            _errorMessage.value = "Please enter a valid 10-digit mobile number."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            // Immediate dispatch with zero artificial delay for instant LCP / feedback
            val (verId, generatedOtp) = authManager.sendOtp(phone, _userName.value, _selectedRole.value)
            _verificationId.value = verId
            _latestGeneratedOtp.value = generatedOtp
            _isOtpSent.value = true
            _isLoading.value = false
            startTimer()
            onSuccess()
        }
    }

    fun verifyOtp(onSuccess: () -> Unit = {}) {
        val otp = _enteredOtp.value.trim()
        if (otp.length != 6) {
            _errorMessage.value = "Please enter the complete 6-digit verification code."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            // Immediate verification with zero artificial delay
            val credential = PhoneNumberAuthCredential(
                verificationId = _verificationId.value ?: "ver_id_local",
                smsCode = otp,
                phoneNumber = _phoneNumber.value
            )
            val success = authManager.verifyWithCredential(credential)
            _isLoading.value = false
            if (success) {
                onSuccess()
            } else {
                _errorMessage.value = "Invalid OTP code. Please enter the 6-digit code received or use ${_latestGeneratedOtp.value}."
            }
        }
    }

    fun autoFillDemoOtp() {
        _enteredOtp.value = _latestGeneratedOtp.value.ifBlank { "123456" }
        _errorMessage.value = null
    }

    fun resendOtp() {
        if (_resendCountdown.value > 0) return
        requestOtp()
    }

    fun resetState() {
        _isOtpSent.value = false
        _enteredOtp.value = ""
        _errorMessage.value = null
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob?.cancel()
        _resendCountdown.value = 45
        timerJob = viewModelScope.launch {
            while (_resendCountdown.value > 0) {
                delay(1000)
                _resendCountdown.value = _resendCountdown.value - 1
            }
        }
    }

    fun getString(key: String): String = AppStrings.get(key, _currentLanguage.value)
}
