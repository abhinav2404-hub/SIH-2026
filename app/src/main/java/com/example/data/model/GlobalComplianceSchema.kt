package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Standardized AI Output & Compliance Verification Schema (Fixed Schema).
 */
@JsonClass(generateAdapter = true)
data class GlobalMrp(
    @Json(name = "value") val value: Double = 0.0,
    @Json(name = "currency") val currency: String = "INR",
    @Json(name = "inclusive_of_taxes") val inclusiveOfTaxes: Boolean = true
)

@JsonClass(generateAdapter = true)
data class GlobalNetQuantity(
    @Json(name = "value") val value: Double = 0.0,
    @Json(name = "unit") val unit: String = "g"
)

@JsonClass(generateAdapter = true)
data class GlobalIngredientItem(
    @Json(name = "name") val name: String,
    @Json(name = "percentage") val percentage: String? = null,
    @Json(name = "is_allergen") val isAllergen: Boolean = false,
    @Json(name = "allergen_category") val allergenCategory: String? = null,
    @Json(name = "purpose") val purpose: String? = null
)

@JsonClass(generateAdapter = true)
data class GlobalViolation(
    @Json(name = "rule_id") val ruleId: String,
    @Json(name = "rule_name") val ruleName: String,
    @Json(name = "detected_value") val detectedValue: String,
    @Json(name = "required_value") val requiredValue: String,
    @Json(name = "evidence") val evidence: String,
    @Json(name = "reason") val reason: String,
    @Json(name = "severity") val severity: String = "MAJOR", // CRITICAL, MAJOR, MINOR
    @Json(name = "penalty_clause") val penaltyClause: String = ""
)

/**
 * Primary standardized Compliance Result returned by AI Engine & Local Compliance Evaluator.
 */
@JsonClass(generateAdapter = true)
data class GlobalComplianceResult(
    @Json(name = "scan_id") val scanId: String = "",
    @Json(name = "ruleset_id") val rulesetId: String = "IN-PCR2011-v2011",
    @Json(name = "product_name") val productName: String = "",
    @Json(name = "brand") val brand: String = "",
    @Json(name = "manufacturer") val manufacturer: String = "",
    @Json(name = "importer") val importer: String = "",
    @Json(name = "mrp") val mrp: GlobalMrp = GlobalMrp(),
    @Json(name = "net_quantity") val netQuantity: GlobalNetQuantity = GlobalNetQuantity(),
    @Json(name = "manufacturing_date") val manufacturingDate: String = "",
    @Json(name = "expiry_date") val expiryDate: String = "",
    @Json(name = "best_before") val bestBefore: String = "",
    @Json(name = "batch_number") val batchNumber: String = "",
    @Json(name = "ingredients") val ingredients: List<String> = emptyList(),
    @Json(name = "allergens") val allergens: List<String> = emptyList(),
    @Json(name = "country_of_origin") val countryOfOrigin: String = "",
    @Json(name = "customer_care") val customerCare: String = "",
    @Json(name = "font_height_mm") val fontHeightMm: Double = 0.0,
    @Json(name = "font_height_required_mm") val fontHeightRequiredMm: Double = 0.0,
    @Json(name = "capture_mode") val captureMode: String = "ONLINE", // ONLINE, QUEUED_OFFLINE
    @Json(name = "sync_status") val syncStatus: String = "SYNCED", // PENDING, SYNCING, SYNCED, CONFLICT, FAILED
    @Json(name = "locale") val locale: String = "en-IN",
    @Json(name = "confidence_score") val confidenceScore: Int = 85, // 0 - 100
    @Json(name = "overall_status") val overallStatus: String = "PASS", // PASS, FAIL, NOT_VERIFIABLE
    @Json(name = "violations") val violations: List<GlobalViolation> = emptyList(),
    @Json(name = "warnings") val warnings: List<String> = emptyList(),
    @Json(name = "not_verifiable_fields") val notVerifiableFields: List<String> = emptyList(),
    @Json(name = "recommendations") val recommendations: List<String> = emptyList(),
    
    // Additional runtime context fields
    @Json(name = "barcode") val barcode: String = "",
    @Json(name = "qr_data") val qrData: String = "",
    @Json(name = "qr_mismatches") val qrMismatches: List<String> = emptyList(),
    @Json(name = "remaining_days") val remainingDays: Int? = null,
    @Json(name = "is_expired") val isExpired: Boolean = false,
    @Json(name = "raw_ocr_text") val rawOcrText: String = "",
    @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
