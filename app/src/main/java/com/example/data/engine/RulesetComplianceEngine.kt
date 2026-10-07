package com.example.data.engine

import com.example.data.model.GlobalComplianceResult
import com.example.data.model.GlobalMrp
import com.example.data.model.GlobalNetQuantity
import com.example.data.model.GlobalViolation
import com.example.data.ruleset.RegulatoryRuleset
import com.example.data.ruleset.RulesetRegistry
import java.util.UUID
import java.util.regex.Pattern

/**
 * Universal Configuration-Driven Ruleset Compliance Engine.
 * Evaluates scans dynamically against the active ruleset_id (e.g. IN-PCR2011-v2011, DEMO-GLOBAL-v1, EU-FIC-1169-2011, US-FDA-LABEL-v1).
 * Does NOT hard-code any country assumptions.
 */
object RulesetComplianceEngine {

    fun evaluate(
        scanId: String = UUID.randomUUID().toString(),
        rulesetId: String = "IN-PCR2011-v2011",
        productName: String,
        brand: String,
        manufacturer: String,
        importer: String = "",
        mrpValue: Double,
        currency: String = "",
        isInclusiveTaxes: Boolean = true,
        netQtyValue: Double,
        netQtyUnit: String,
        mfgDate: String,
        expiryDate: String,
        bestBefore: String = "",
        batchNumber: String = "",
        ingredients: List<String> = emptyList(),
        allergens: List<String> = emptyList(),
        countryOfOrigin: String = "",
        customerCare: String = "",
        measuredFontHeightMm: Double = 0.0,
        barcode: String = "",
        qrData: String = "",
        rawOcrText: String = "",
        captureMode: String = "ONLINE",
        syncStatus: String = "SYNCED",
        locale: String = "en-IN"
    ): GlobalComplianceResult {
        val ruleset: RegulatoryRuleset = RulesetRegistry.getRuleset(rulesetId)
        val violations = mutableListOf<GlobalViolation>()
        val warnings = mutableListOf<String>()
        val notVerifiable = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        // 1. Calculate Required Font Height for this Ruleset
        var requiredFontHeight = 0.0
        val effectiveNetQtyGrams = when (netQtyUnit.lowercase()) {
            "kg", "l", "litre", "litres" -> netQtyValue * 1000.0
            "oz" -> netQtyValue * 28.3495
            "lb" -> netQtyValue * 453.592
            else -> netQtyValue
        }

        val matchingFontRule = ruleset.fontHeightRules.firstOrNull {
            effectiveNetQtyGrams >= it.minNetQuantityGrams && effectiveNetQtyGrams <= it.maxNetQuantityGrams
        } ?: ruleset.fontHeightRules.lastOrNull()

        if (matchingFontRule != null) {
            requiredFontHeight = matchingFontRule.requiredFontHeightMm
        }

        // 2. Date & Expiry Programmatic Validation
        val dateResult = DateValidationEngine.validateDates(
            mfgDateText = mfgDate,
            expiryDateText = expiryDate,
            bestBeforePeriodText = bestBefore
        )

        // 3. Barcode / QR Code Consistency Validation
        val barcodeResult = BarcodeConsistencyEngine.verifyConsistency(
            barcode = barcode,
            qrData = qrData,
            ocrMrpValue = mrpValue,
            ocrNetQtyValue = netQtyValue,
            ocrBatch = batchNumber
        )
        if (!barcodeResult.isConsistent) {
            warnings.addAll(barcodeResult.mismatches)
        }
        warnings.addAll(barcodeResult.warnings)

        // 4. Rule-by-Rule Evaluation based on Active Ruleset
        for (reg in ruleset.regulations) {
            when (reg.ruleId) {
                // Rule 6(1)(a) / Manufacturer declaration
                "PCR-R6-1A", "CODEX-SEC-4.4", "FDA-21CFR-101.5" -> {
                    if (manufacturer.isBlank() && importer.isBlank()) {
                        violations.add(
                            GlobalViolation(
                                ruleId = reg.ruleId,
                                ruleName = reg.name,
                                detectedValue = "Missing / Omitted",
                                requiredValue = "Full business name and postal address of manufacturer / packer / importer",
                                evidence = "OCR scanning failed to locate manufacturer or importer details on label.",
                                reason = "Mandatory identity of producer is absent.",
                                severity = reg.severity,
                                penaltyClause = reg.penaltyClause
                            )
                        )
                    }
                }

                // Rule 6(1)(b) / Product Identity
                "PCR-R6-1B", "CODEX-SEC-4.1", "EU-FIC-ART-9-1A", "FDA-21CFR-101.3" -> {
                    if (productName.isBlank()) {
                        notVerifiable.add("Product Name / Statement of Identity")
                    }
                }

                // Rule 6(1)(c) / Net Quantity
                "PCR-R6-1C", "CODEX-SEC-4.3", "EU-FIC-ART-9-1E", "FDA-21CFR-101.105" -> {
                    if (netQtyValue <= 0.0 || netQtyUnit.isBlank()) {
                        violations.add(
                            GlobalViolation(
                                ruleId = reg.ruleId,
                                ruleName = reg.name,
                                detectedValue = "Missing or unquantified",
                                requiredValue = "Net contents in standard SI units (e.g., g, kg, ml, L)",
                                evidence = "No metric net quantity declaration located in Principal Display Panel.",
                                reason = "Consumers cannot ascertain quantity.",
                                severity = reg.severity,
                                penaltyClause = reg.penaltyClause
                            )
                        )
                    }
                }

                // Rule 6(1)(da) / MRP & Taxes (Specific to India PCR 2011)
                "PCR-R6-1DA" -> {
                    if (mrpValue <= 0.0) {
                        violations.add(
                            GlobalViolation(
                                ruleId = reg.ruleId,
                                ruleName = reg.name,
                                detectedValue = "Missing / Omitted",
                                requiredValue = "MRP ₹ XX.XX (inclusive of all taxes)",
                                evidence = "Price tag declaration absent from package.",
                                reason = "Sale of packaged commodity without declared MRP is prohibited.",
                                severity = reg.severity,
                                penaltyClause = reg.penaltyClause
                            )
                        )
                    } else if (!isInclusiveTaxes && !rawOcrText.contains("tax", ignoreCase = true) && !rawOcrText.contains("incl", ignoreCase = true)) {
                        warnings.add("Rule 6(1)(da) Warning: MRP ₹$mrpValue does not prominently state '(inclusive of all taxes)'.")
                    }
                }

                // Rule 6(1)(e) / Unit Sale Price (India PCR Amendment 2022)
                "PCR-R6-1E" -> {
                    if (mrpValue > 0 && netQtyValue > 0) {
                        // Check if USP is declared or computably present
                        val hasUspInText = rawOcrText.contains("USP", ignoreCase = true) || rawOcrText.contains("/ g", ignoreCase = true) || rawOcrText.contains("/ 100g", ignoreCase = true) || rawOcrText.contains("/ ml", ignoreCase = true) || rawOcrText.contains("/ kg", ignoreCase = true)
                        if (!hasUspInText && effectiveNetQtyGrams > 0) {
                            recommendations.add("Ensure Unit Sale Price (₹/g or ₹/100g) is legibly printed alongside MRP for retail compliance.")
                        }
                    }
                }

                // Date Declarations / Durability
                "PCR-R6-1D", "CODEX-SEC-4.7", "EU-FIC-ART-9-1F" -> {
                    if (!dateResult.isValid) {
                        if (dateResult.isAmbiguous) {
                            notVerifiable.add("Expiry / Date of Durability")
                        } else {
                            violations.add(
                                GlobalViolation(
                                    ruleId = reg.ruleId,
                                    ruleName = reg.name,
                                    detectedValue = dateResult.expiryDateFormatted ?: "Unreadable",
                                    requiredValue = "Valid chronological date format (DD/MM/YYYY or MM/YYYY)",
                                    evidence = dateResult.explanation,
                                    reason = if (dateResult.isExpired) "Expired product offered for distribution." else "Invalid date marking.",
                                    severity = reg.severity,
                                    penaltyClause = reg.penaltyClause
                                )
                            )
                        }
                    }
                }

                // Rule 6(1)(g) / Customer Care
                "PCR-R6-1G" -> {
                    if (customerCare.isBlank() && !rawOcrText.contains("care", ignoreCase = true) && !rawOcrText.contains("helpline", ignoreCase = true) && !rawOcrText.contains("1800", ignoreCase = true)) {
                        violations.add(
                            GlobalViolation(
                                ruleId = reg.ruleId,
                                ruleName = reg.name,
                                detectedValue = "Missing consumer redressal",
                                requiredValue = "Name, telephone, address, and email for grievance redressal",
                                evidence = "No customer care email or helpline number found on package.",
                                reason = "Consumer right to grievance redressal infringed.",
                                severity = reg.severity,
                                penaltyClause = reg.penaltyClause
                            )
                        )
                    }
                }

                // Schedule II / Font Height Check
                "PCR-R9" -> {
                    if (measuredFontHeightMm > 0 && requiredFontHeight > 0) {
                        if (measuredFontHeightMm < (requiredFontHeight * 0.9)) { // Allow 10% tolerance for optical angle
                            violations.add(
                                GlobalViolation(
                                    ruleId = reg.ruleId,
                                    ruleName = reg.name,
                                    detectedValue = "${measuredFontHeightMm} mm",
                                    requiredValue = "${requiredFontHeight} mm minimum (Schedule II for ${matchingFontRule?.areaDescription})",
                                    evidence = "Optical measurement detected numeral font height of ${measuredFontHeightMm}mm.",
                                    reason = "Declaration font height is below statutory legibility threshold.",
                                    severity = reg.severity,
                                    penaltyClause = reg.penaltyClause
                                )
                            )
                        }
                    }
                }

                // Ingredients & Allergens
                "FSSAI-R2020", "CODEX-SEC-4.2", "EU-FIC-ART-9-1B", "EU-FIC-ART-9-1C", "FDA-FALCPA-2004" -> {
                    if (allergens.isEmpty() && rawOcrText.contains("contain", ignoreCase = true) && (rawOcrText.contains("wheat", ignoreCase = true) || rawOcrText.contains("milk", ignoreCase = true) || rawOcrText.contains("soy", ignoreCase = true) || rawOcrText.contains("nut", ignoreCase = true))) {
                        warnings.add("Potential allergen keywords detected in ingredient text; verify bold allergen highlight on package.")
                    }
                }
            }
        }

        // 5. Compute Programmatic Confidence Score (0 - 100)
        var confidence = 95
        if (rawOcrText.length < 50) confidence -= 25
        if (productName.isBlank()) confidence -= 15
        if (mrpValue <= 0.0) confidence -= 10
        if (netQtyValue <= 0.0) confidence -= 10
        if (mfgDate.isBlank() && expiryDate.isBlank()) confidence -= 15
        if (dateResult.isAmbiguous) confidence -= 10
        if (!barcodeResult.isConsistent) confidence -= 15
        if (measuredFontHeightMm <= 0.0) confidence -= 5
        val finalConfidenceScore = Math.max(20, Math.min(100, confidence))

        // 6. Compute Strict 3-State Compliance Verdict
        val overallStatus = when {
            violations.isNotEmpty() || dateResult.isExpired -> "FAIL"
            notVerifiable.size >= 2 || finalConfidenceScore < 60 -> "NOT_VERIFIABLE"
            else -> "PASS"
        }

        // 7. Dynamic Recommendations
        if (overallStatus == "FAIL") {
            recommendations.add("Issue formal statutory notice under active ruleset (${ruleset.rulesetId}).")
        } else if (overallStatus == "NOT_VERIFIABLE") {
            recommendations.add("Re-scan package in well-lit conditions with camera parallel to Principal Display Panel.")
        } else {
            recommendations.add("Package complies with all verified statutory declarations under ${ruleset.rulesetId}.")
        }

        return GlobalComplianceResult(
            scanId = scanId,
            rulesetId = ruleset.rulesetId,
            productName = if (productName.isNotBlank()) productName else "Unidentified Package Item",
            brand = brand,
            manufacturer = manufacturer,
            importer = importer,
            mrp = GlobalMrp(
                value = mrpValue,
                currency = if (currency.isNotBlank()) currency else ruleset.currency,
                inclusiveOfTaxes = isInclusiveTaxes
            ),
            netQuantity = GlobalNetQuantity(
                value = netQtyValue,
                unit = if (netQtyUnit.isNotBlank()) netQtyUnit else "g"
            ),
            manufacturingDate = dateResult.mfgDateFormatted ?: mfgDate,
            expiryDate = dateResult.expiryDateFormatted ?: expiryDate,
            bestBefore = bestBefore,
            batchNumber = batchNumber,
            ingredients = ingredients,
            allergens = allergens,
            countryOfOrigin = if (countryOfOrigin.isNotBlank()) countryOfOrigin else ruleset.countryName,
            customerCare = if (customerCare.isNotBlank()) customerCare else ruleset.consumerHelpline,
            fontHeightMm = measuredFontHeightMm,
            fontHeightRequiredMm = requiredFontHeight,
            captureMode = captureMode,
            syncStatus = syncStatus,
            locale = locale,
            confidenceScore = finalConfidenceScore,
            overallStatus = overallStatus,
            violations = violations,
            warnings = warnings,
            notVerifiableFields = notVerifiable,
            recommendations = recommendations,
            barcode = barcode,
            qrData = qrData,
            qrMismatches = barcodeResult.mismatches,
            remainingDays = dateResult.remainingDays,
            isExpired = dateResult.isExpired,
            rawOcrText = rawOcrText,
            timestamp = System.currentTimeMillis()
        )
    }
}
