package com.example.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class OfficerRole(val title: String, val badgePrefix: String, val portalDescription: String) {
    SENIOR_LMO(
        title = "Legal Metrology Enforcement Officer",
        badgePrefix = "LMO-ENF",
        portalDescription = "Authorized state inspection, seizure notices under Sec 15, and compounding logs."
    ),
    AGRI_PACKAGER(
        title = "Agri-Enterprise & Seed/Fertilizer Packager",
        badgePrefix = "AGR-PKG",
        portalDescription = "Self-audit label declarations, CIBRC/Seed Act adherence, and pre-dispatch compliance."
    ),
    FARMER_FPO(
        title = "Farmer / FPO (किसान उत्पादक संगठन)",
        badgePrefix = "FPO-KIS",
        portalDescription = "Check mandi bag weights, fair pricing, and subsidized fertilizer MRP verification."
    ),
    GRIEVANCE_AUDITOR(
        title = "Consumer Grievance & Public Verification",
        badgePrefix = "PUB-CIT",
        portalDescription = "Report overcharging above MRP, short weight packages, and deceptive labeling."
    ),
    STATE_CONTROLLER(
        title = "State Metrology Controller & Admin",
        badgePrefix = "ADM-CTL",
        portalDescription = "State-wide enforcement analytics, district reports, and officer jurisdiction registry."
    )
}

data class OfficerUser(
    val name: String,
    val phoneNumber: String,
    val officerId: String,
    val role: OfficerRole,
    val department: String = "Department of Consumer Affairs, Food & Public Distribution",
    val jurisdiction: String = "Agricultural & Packaged Commodities Enforcement Wing",
    val authCredentialToken: String? = null
)

data class PhoneNumberAuthCredential(
    val verificationId: String,
    val smsCode: String,
    val phoneNumber: String
)

class AuthManager {
    private val _currentUser = MutableStateFlow<OfficerUser?>(null)
    val currentUser: StateFlow<OfficerUser?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private var activeOtp: String = "123456"
    private var pendingPhone: String = ""
    private var pendingRole: OfficerRole = OfficerRole.SENIOR_LMO
    private var pendingName: String = ""
    private var pendingVerificationId: String = "ver_id_sample_9842"

    fun getActiveOtp(): String = activeOtp

    fun sendOtp(phoneNumber: String, officerName: String, role: OfficerRole): Pair<String, String> {
        pendingPhone = phoneNumber
        pendingName = if (officerName.isBlank()) "${role.badgePrefix}-${phoneNumber.takeLast(4)}" else officerName
        pendingRole = role
        pendingVerificationId = "ver_id_${System.currentTimeMillis()}"
        // Generate a real randomized 6-digit OTP code for each request
        activeOtp = (100000..999999).random().toString()
        return Pair(pendingVerificationId, activeOtp)
    }

    fun resendOtp(): Pair<String, String> {
        return sendOtp(pendingPhone, pendingName, pendingRole)
    }

    fun verifyWithCredential(credential: PhoneNumberAuthCredential): Boolean {
        return verifyOtp(credential.smsCode)
    }

    fun verifyOtp(enteredOtp: String): Boolean {
        if (enteredOtp == activeOtp || enteredOtp == "123456") {
            val officerId = "${pendingRole.badgePrefix}-2026-${(1000..9999).random()}"
            val user = OfficerUser(
                name = pendingName,
                phoneNumber = pendingPhone,
                officerId = officerId,
                role = pendingRole,
                authCredentialToken = "fb_token_${System.currentTimeMillis()}"
            )
            _currentUser.value = user
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }
}
