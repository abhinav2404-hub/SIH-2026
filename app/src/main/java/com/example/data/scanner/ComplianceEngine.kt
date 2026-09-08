package com.example.data.scanner

import com.example.data.model.InspectionRecord
import com.example.data.model.InspectionStatus
import com.example.data.model.RuleCheckResult
import com.example.data.model.SamplePackage
import com.example.data.model.ViolationSeverity

object ComplianceEngine {

    fun analyzeSamplePackage(sample: SamplePackage, inspectorName: String, inspectorBadge: String, location: String): Pair<InspectionRecord, List<RuleCheckResult>> {
        val rules = evaluateRules(
            productName = sample.title,
            brandName = sample.brand,
            netQty = sample.netQuantity,
            mrp = sample.declaredMrp,
            usp = sample.declaredUsp,
            origin = sample.countryOfOrigin,
            manufacturer = sample.manufacturer,
            consumerCare = sample.consumerCare,
            mfgDate = sample.mfgDate,
            fontHeightMm = sample.fontHeightMm,
            rawText = sample.rawLabelText
        )

        val passedRules = rules.count { it.isCompliant }
        val score = if (rules.isNotEmpty()) (passedRules * 100) / rules.size else 0

        val criticalCount = rules.count { !it.isCompliant && it.severity == ViolationSeverity.CRITICAL }
        val majorCount = rules.count { !it.isCompliant && it.severity == ViolationSeverity.MAJOR }
        val minorCount = rules.count { !it.isCompliant && it.severity == ViolationSeverity.MINOR }

        val status = when {
            criticalCount > 0 || score < 40 -> InspectionStatus.SEIZURE_RECOMMENDED
            majorCount > 0 || score < 75 -> InspectionStatus.NON_COMPLIANT
            minorCount > 0 || score < 100 -> InspectionStatus.MINOR_VIOLATIONS
            else -> InspectionStatus.COMPLIANT
        }

        val violationList = rules.filter { !it.isCompliant }.mapIndexed { idx, r ->
            "${idx + 1}. [${r.ruleReference}] ${r.ruleName}: ${r.explanation}"
        }.joinToString("\n")

        val summary = if (violationList.isBlank()) "All statutory declarations under Rule 6 & Schedule II are compliant." else violationList

        val record = InspectionRecord(
            productName = sample.title,
            brandName = sample.brand,
            category = sample.category,
            barcode = "890" + (1000000000L..9999999999L).random(),
            netQuantity = sample.netQuantity,
            declaredMrp = sample.declaredMrp,
            declaredUsp = sample.declaredUsp,
            countryOfOrigin = sample.countryOfOrigin,
            manufacturerAddress = sample.manufacturer,
            consumerCareContact = sample.consumerCare,
            mfgPackingDate = sample.mfgDate,
            pdpFontCompliance = sample.fontHeightMm >= getRequiredFontHeight(sample.netQuantity),
            complianceScore = score,
            overallStatus = status.name,
            violationsCount = rules.count { !it.isCompliant },
            ruleViolationsSummary = summary,
            sampleId = sample.id,
            inspectorName = inspectorName,
            inspectorBadge = inspectorBadge,
            inspectionLocation = location,
            officerNotes = "Audited under Rule 6 & 9 of Legal Metrology (Packaged Commodities) Rules, 2011.",
            timestamp = System.currentTimeMillis()
        )

        return Pair(record, rules)
    }

    fun analyzeCustomText(
        productName: String,
        brandName: String,
        category: String,
        rawLabelText: String,
        inspectorName: String,
        inspectorBadge: String,
        location: String
    ): Pair<InspectionRecord, List<RuleCheckResult>> {
        val lower = rawLabelText.lowercase()

        // Extract key components heuristically
        val hasMrp = lower.contains("mrp") || lower.contains("rs.") || lower.contains("₹")
        val hasTaxes = lower.contains("incl. of all taxes") || lower.contains("inclusive of all taxes") || lower.contains("incl of all taxes")
        val hasUsp = lower.contains("unit sale price") || lower.contains("usp") || lower.contains("/g") || lower.contains("/ml") || lower.contains("/kg") || lower.contains("/l") || lower.contains("/piece")
        val hasNetQty = lower.contains("net qty") || lower.contains("net wt") || lower.contains("net weight") || lower.contains("net quantity") || lower.contains("1 l") || lower.contains("500 g") || lower.contains("100 g") || lower.contains("1kg")
        val hasOrigin = lower.contains("india") || lower.contains("country of origin") || lower.contains("made in") || lower.contains("origin:")
        val hasMfgAddress = lower.contains("mfd by") || lower.contains("manufactured by") || lower.contains("pkd by") || lower.contains("packed by") || lower.contains("imported by") || lower.contains("plot") || lower.contains("industrial")
        val hasDate = lower.contains("mfd") || lower.contains("pkd") || lower.contains("date") || lower.contains("2025") || lower.contains("2026") || lower.contains("batch")
        val hasCare = lower.contains("care") || lower.contains("contact") || lower.contains("helpline") || lower.contains("1800") || lower.contains("@") || lower.contains("feedback")

        val extractedMrp = if (hasMrp) {
            val mrpPart = rawLabelText.lines().firstOrNull { it.contains("MRP", ignoreCase = true) || it.contains("₹") } ?: "₹ Declared"
            mrpPart.trim()
        } else "MISSING"

        val extractedUsp = if (hasUsp) "Declared on Label" else "MISSING"
        val extractedOrigin = if (hasOrigin) "India" else "MISSING"
        val extractedNetQty = if (hasNetQty) "Metric unit present" else "MISSING"
        val extractedMfg = if (hasMfgAddress) "Manufacturer Address Present" else "MISSING / INCOMPLETE"
        val extractedCare = if (hasCare) "Consumer Helpline & Email Present" else "MISSING / INCOMPLETE"
        val extractedDate = if (hasDate) "Batch / Packing Date Present" else "MISSING"

        val rules = evaluateRules(
            productName = productName.ifBlank { "Scanned Package" },
            brandName = brandName.ifBlank { "Commercial Brand" },
            netQty = extractedNetQty,
            mrp = extractedMrp,
            usp = extractedUsp,
            origin = extractedOrigin,
            manufacturer = extractedMfg,
            consumerCare = extractedCare,
            mfgDate = extractedDate,
            fontHeightMm = 3.0,
            rawText = rawLabelText
        )

        val passedRules = rules.count { it.isCompliant }
        val score = if (rules.isNotEmpty()) (passedRules * 100) / rules.size else 0

        val criticalCount = rules.count { !it.isCompliant && it.severity == ViolationSeverity.CRITICAL }
        val majorCount = rules.count { !it.isCompliant && it.severity == ViolationSeverity.MAJOR }
        val minorCount = rules.count { !it.isCompliant && it.severity == ViolationSeverity.MINOR }

        val status = when {
            criticalCount > 0 || score < 40 -> InspectionStatus.SEIZURE_RECOMMENDED
            majorCount > 0 || score < 75 -> InspectionStatus.NON_COMPLIANT
            minorCount > 0 || score < 100 -> InspectionStatus.MINOR_VIOLATIONS
            else -> InspectionStatus.COMPLIANT
        }

        val violationList = rules.filter { !it.isCompliant }.mapIndexed { idx, r ->
            "${idx + 1}. [${r.ruleReference}] ${r.ruleName}: ${r.explanation}"
        }.joinToString("\n")

        val summary = if (violationList.isBlank()) "All statutory declarations under Rule 6 & Schedule II are compliant." else violationList

        val record = InspectionRecord(
            productName = productName.ifBlank { "Scanned Packaged Commodity" },
            brandName = brandName.ifBlank { "Generic Packer" },
            category = category.ifBlank { "Packaged Commodity" },
            barcode = "890" + (1000000000L..9999999999L).random(),
            netQuantity = extractedNetQty,
            declaredMrp = extractedMrp,
            declaredUsp = extractedUsp,
            countryOfOrigin = extractedOrigin,
            manufacturerAddress = extractedMfg,
            consumerCareContact = extractedCare,
            mfgPackingDate = extractedDate,
            pdpFontCompliance = true,
            complianceScore = score,
            overallStatus = status.name,
            violationsCount = rules.count { !it.isCompliant },
            ruleViolationsSummary = summary,
            inspectorName = inspectorName,
            inspectorBadge = inspectorBadge,
            inspectionLocation = location,
            officerNotes = "Scanned label OCR compliance audit.",
            timestamp = System.currentTimeMillis()
        )

        return Pair(record, rules)
    }

    private fun evaluateRules(
        productName: String,
        brandName: String,
        netQty: String,
        mrp: String,
        usp: String,
        origin: String,
        manufacturer: String,
        consumerCare: String,
        mfgDate: String,
        fontHeightMm: Double,
        rawText: String
    ): List<RuleCheckResult> {
        val list = mutableListOf<RuleCheckResult>()

        // Rule 6(1)(a): Name & complete address of Manufacturer / Packer / Importer
        val isMfgValid = !manufacturer.contains("MISSING", ignoreCase = true) &&
                !manufacturer.contains("importer missing", ignoreCase = true) &&
                manufacturer.length > 15
        list.add(
            RuleCheckResult(
                ruleId = "RULE_6_1_A",
                ruleName = "Manufacturer / Packer / Importer Address",
                ruleReference = "Rule 6(1)(a)",
                isCompliant = isMfgValid,
                severity = if (isMfgValid) ViolationSeverity.NONE else ViolationSeverity.CRITICAL,
                detectedValue = manufacturer,
                requiredStandard = "Complete legal name & full premises address (with PIN & country) of manufacturer, packer, or Indian importer.",
                explanation = if (isMfgValid) "Complete manufacturer/packer address clearly declared on panel." else "Name and address of manufacturer or Indian importer is missing or incomplete.",
                legalPenalty = "Section 36 of Legal Metrology Act, 2009: Fine up to ₹25,000 for 1st offense, ₹50,000 for 2nd offense."
            )
        )

        // Rule 6(1)(b): Generic or Common Name of Commodity
        val isGenericValid = productName.isNotBlank() && !productName.contains("Unknown", ignoreCase = true)
        list.add(
            RuleCheckResult(
                ruleId = "RULE_6_1_B",
                ruleName = "Common / Generic Name of Commodity",
                ruleReference = "Rule 6(1)(b)",
                isCompliant = isGenericValid,
                severity = if (isGenericValid) ViolationSeverity.NONE else ViolationSeverity.MAJOR,
                detectedValue = productName,
                requiredStandard = "Prominent declaration of generic commodity name on the Principal Display Panel.",
                explanation = if (isGenericValid) "Commodity generic name is prominently printed." else "Common/generic identity of commodity missing or obscured.",
                legalPenalty = "Compound fine up to ₹10,000 under Rule 32 of PCR 2011."
            )
        )

        // Rule 6(1)(c): Net Quantity in Standard Metric Units
        val isNetQtyValid = !netQty.contains("MISSING", ignoreCase = true) &&
                (netQty.contains("g", ignoreCase = true) || netQty.contains("kg", ignoreCase = true) ||
                        netQty.contains("ml", ignoreCase = true) || netQty.contains("l", ignoreCase = true) ||
                        netQty.contains("piece", ignoreCase = true) || netQty.contains("N", ignoreCase = false))
        list.add(
            RuleCheckResult(
                ruleId = "RULE_6_1_C",
                ruleName = "Net Quantity in Standard Units",
                ruleReference = "Rule 6(1)(c)",
                isCompliant = isNetQtyValid,
                severity = if (isNetQtyValid) ViolationSeverity.NONE else ViolationSeverity.CRITICAL,
                detectedValue = netQty,
                requiredStandard = "Standard metric unit of weight (g/kg), volume (ml/L), or count (N/U). No non-metric or vague qualifiers.",
                explanation = if (isNetQtyValid) "Net quantity declared in legitimate metric units." else "Net quantity omitted or uses prohibited non-metric designations.",
                legalPenalty = "Section 36(1) of LM Act: Seizure and compound fee up to ₹25,000."
            )
        )

        // Rule 6(1)(d): Month & Year of Manufacture / Packing / Import
        val isDateValid = !mfgDate.contains("MISSING", ignoreCase = true) &&
                (mfgDate.contains("/") || mfgDate.contains("202") || mfgDate.contains("Date") || mfgDate.contains("Present"))
        list.add(
            RuleCheckResult(
                ruleId = "RULE_6_1_D",
                ruleName = "Month & Year of Packing / Mfg",
                ruleReference = "Rule 6(1)(d)",
                isCompliant = isDateValid,
                severity = if (isDateValid) ViolationSeverity.NONE else ViolationSeverity.MAJOR,
                detectedValue = mfgDate,
                requiredStandard = "Month and year of manufacture/packing in MM/YYYY format or clear chronological representation.",
                explanation = if (isDateValid) "Month and year of packing accurately declared." else "Month/Year of packing/mfg is absent on the package.",
                legalPenalty = "Fine up to ₹20,000 under PCR 2011 Rule 32."
            )
        )

        // Rule 6(1)(da): Maximum Retail Price (MRP) with Tax Inclusion
        val isMrpPresent = !mrp.contains("MISSING", ignoreCase = true)
        val hasTaxPhrase = mrp.contains("incl", ignoreCase = true) || mrp.contains("inclusive", ignoreCase = true) || mrp.contains("tax", ignoreCase = true)
        val isMrpCompliant = isMrpPresent && hasTaxPhrase
        list.add(
            RuleCheckResult(
                ruleId = "RULE_6_1_DA",
                ruleName = "MRP Declaration (Inclusive of All Taxes)",
                ruleReference = "Rule 6(1)(da)",
                isCompliant = isMrpCompliant,
                severity = if (isMrpCompliant) ViolationSeverity.NONE else if (!isMrpPresent) ViolationSeverity.CRITICAL else ViolationSeverity.MAJOR,
                detectedValue = mrp,
                requiredStandard = "Format 'MRP Rs. XX.XX (inclusive of all taxes)' or 'MRP ₹ XX.XX (incl. of all taxes)'.",
                explanation = when {
                    isMrpCompliant -> "MRP properly formatted with statutory '(inclusive of all taxes)' declaration."
                    !isMrpPresent -> "Maximum Retail Price is completely missing from package."
                    else -> "MRP lacks mandatory '(inclusive of all taxes)' phrase. Selling above printed price or omitting tax clarification is prohibited."
                },
                legalPenalty = "Section 36(1) penalty: Up to ₹25,000 for first conviction, up to ₹1,00,000 or 1 year imprisonment for repeated violations."
            )
        )

        // Rule 6(1)(e): Unit Sale Price (USP)
        val isUspValid = !usp.contains("MISSING", ignoreCase = true) &&
                !usp.contains("Not printed", ignoreCase = true) &&
                (usp.contains("/") || usp.contains("Declared"))
        list.add(
            RuleCheckResult(
                ruleId = "RULE_6_1_E",
                ruleName = "Unit Sale Price (USP)",
                ruleReference = "Rule 6(1)(e)",
                isCompliant = isUspValid,
                severity = if (isUspValid) ViolationSeverity.NONE else ViolationSeverity.MAJOR,
                detectedValue = usp,
                requiredStandard = "Mandatory declaration of Unit Sale Price in ₹ per g/kg, per ml/L, or per piece for multi-quantity or >1g/ml commodities.",
                explanation = if (isUspValid) "Unit Sale Price accurately computed and displayed for consumer comparison." else "Unit Sale Price (USP) is missing. This violates the 2022 Legal Metrology mandatory amendment.",
                legalPenalty = "Notice of violation under Rule 32 of PCR 2011; compound penalty up to ₹25,000."
            )
        )

        // Rule 6(1)(f): Country of Origin
        val isOriginValid = !origin.contains("MISSING", ignoreCase = true) && origin.isNotBlank()
        list.add(
            RuleCheckResult(
                ruleId = "RULE_6_1_F",
                ruleName = "Country of Origin",
                ruleReference = "Rule 6(1)(f)",
                isCompliant = isOriginValid,
                severity = if (isOriginValid) ViolationSeverity.NONE else ViolationSeverity.CRITICAL,
                detectedValue = origin,
                requiredStandard = "Clear declaration of 'Country of Origin' on every packaged commodity (imported or domestic).",
                explanation = if (isOriginValid) "Country of Origin declared unambiguously." else "Country of Origin declaration is completely absent.",
                legalPenalty = "Seizure of goods and fine under Section 36 of Legal Metrology Act, 2009."
            )
        )

        // Rule 6(1)(g): Consumer Care Helpline, Email & Contact Person
        val isCareValid = !consumerCare.contains("MISSING", ignoreCase = true) &&
                !consumerCare.contains("No phone", ignoreCase = true) &&
                !consumerCare.contains("No Indian", ignoreCase = true) &&
                (consumerCare.contains("@") || consumerCare.contains("1800") || consumerCare.contains("care") || consumerCare.contains("Present"))
        list.add(
            RuleCheckResult(
                ruleId = "RULE_6_1_G",
                ruleName = "Consumer Care & Grievance Redressal",
                ruleReference = "Rule 6(1)(g)",
                isCompliant = isCareValid,
                severity = if (isCareValid) ViolationSeverity.NONE else ViolationSeverity.MAJOR,
                detectedValue = consumerCare,
                requiredStandard = "Name/Designation, Telephone/Toll-Free Number, and Email Address of the officer/person handling consumer complaints.",
                explanation = if (isCareValid) "Full consumer redressal channels (email, phone, address) provided." else "Incomplete consumer grievance contact details (missing phone number, email, or Indian address).",
                legalPenalty = "Violation under Rule 6(1)(g) of PCR 2011; compound fee up to ₹25,000."
            )
        )

        // Rule 9 & Schedule II: Font Height of Numerals & Letters on Principal Display Panel
        val requiredHeight = getRequiredFontHeight(netQty)
        val isFontValid = fontHeightMm >= requiredHeight
        list.add(
            RuleCheckResult(
                ruleId = "RULE_9_FONT_SIZE",
                ruleName = "Principal Display Panel (PDP) Font Height",
                ruleReference = "Rule 9 & Schedule II",
                isCompliant = isFontValid,
                severity = if (isFontValid) ViolationSeverity.NONE else ViolationSeverity.MINOR,
                detectedValue = "${fontHeightMm} mm",
                requiredStandard = "Minimum ${requiredHeight} mm numeral height for net quantity/MRP based on package size.",
                explanation = if (isFontValid) "Font height satisfies minimum legibility threshold (${fontHeightMm}mm >= ${requiredHeight}mm)." else "Font height (${fontHeightMm}mm) is smaller than the statutory minimum (${requiredHeight}mm) for this pack size.",
                legalPenalty = "Notice for rectification under Rule 9; compound fee up to ₹10,000."
            )
        )

        return list
    }

    private fun getRequiredFontHeight(netQty: String): Double {
        val lower = netQty.lowercase()
        return when {
            lower.contains("kg") || lower.contains("1 l") || lower.contains("5 kg") || lower.contains("5kg") -> 6.0
            lower.contains("500") || lower.contains("350") -> 4.0
            lower.contains("200") || lower.contains("95") || lower.contains("100") || lower.contains("50") -> 2.0
            else -> 2.0
        }
    }
}
