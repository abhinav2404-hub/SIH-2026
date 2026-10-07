package com.example.data.model

enum class ComplianceStatus {
    PASS,           // 🟢
    WARNING,        // 🟠
    FAIL,           // 🔴
    NOT_VERIFIABLE; // ⚪

    val emoji: String
        get() = when (this) {
            PASS -> "🟢"
            WARNING -> "🟠"
            FAIL -> "🔴"
            NOT_VERIFIABLE -> "⚪"
        }

    companion object {
        fun fromString(statusStr: String): ComplianceStatus {
            return when (statusStr.uppercase().trim()) {
                "PASS", "COMPLIANT", "SUCCESS", "VALID" -> PASS
                "WARNING", "REVIEW", "MINOR", "ADVISORY" -> WARNING
                "FAIL", "NON_COMPLIANT", "CRITICAL", "VIOLATION" -> FAIL
                else -> NOT_VERIFIABLE
            }
        }
    }
}

/**
 * Domain model representing a single regulatory/metrology rule check.
 */
data class ComplianceCheck(
    val ruleId: String = "",
    val ruleName: String,
    val status: ComplianceStatus = ComplianceStatus.PASS,
    val detectedValue: String = "",
    val requiredValue: String = "",
    val evidenceSnippet: String = "",
    val reasoning: String = "",
    val penaltyClause: String? = null,
    val statutoryRef: String? = null
)
