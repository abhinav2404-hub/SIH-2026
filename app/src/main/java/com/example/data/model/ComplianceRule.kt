package com.example.data.model

data class RuleCheckResult(
    val ruleId: String,
    val ruleName: String,
    val ruleReference: String,
    val isCompliant: Boolean,
    val severity: ViolationSeverity,
    val detectedValue: String,
    val requiredStandard: String,
    val explanation: String,
    val legalPenalty: String
)

enum class ViolationSeverity(val label: String) {
    NONE("Compliant"),
    MINOR("Minor Defect"),
    MAJOR("Major Violation"),
    CRITICAL("Severe / Seizure Warranted")
}

enum class InspectionStatus(val label: String) {
    COMPLIANT("100% Compliant"),
    MINOR_VIOLATIONS("Minor Non-Compliance"),
    NON_COMPLIANT("Statutory Violations Flagged"),
    SEIZURE_RECOMMENDED("Seizure Notice Recommended")
}

data class SamplePackage(
    val id: String,
    val title: String,
    val brand: String,
    val category: String,
    val netQuantity: String,
    val declaredMrp: String,
    val declaredUsp: String,
    val countryOfOrigin: String,
    val manufacturer: String,
    val consumerCare: String,
    val mfgDate: String,
    val expiryDate: String,
    val fontHeightMm: Double,
    val pdpAreaSqCm: Double,
    val expectedComplianceScore: Int,
    val expectedStatus: InspectionStatus,
    val rawLabelText: String,
    val description: String,
    val violationSummary: String
)
