package com.example.data.ruleset

/**
 * RulesetRegistry provides configuration-driven regulatory rulesets.
 * The compliance engine queries this registry via ruleset_id and evaluates compliance dynamically.
 */
object RulesetRegistry {

    // 1. India - Legal Metrology (Packaged Commodities) Rules, 2011 + FSSAI
    val INDIA_PCR_2011 = RegulatoryRuleset(
        rulesetId = "IN-PCR2011-v2011",
        countryCode = "IN",
        countryName = "India",
        flagEmoji = "🇮🇳",
        currency = "INR",
        currencySymbol = "₹",
        languages = listOf("en-IN", "hi-IN", "ta-IN", "bn-IN", "te-IN", "mr-IN"),
        isDefault = true,
        description = "Legal Metrology (Packaged Commodities) Rules 2011, FSSAI Packaging & Labelling Reg. 2020 & AGMARK",
        consumerHelpline = "National Consumer Helpline: 1915 / 1800-11-4000",
        standardUnits = listOf("g", "kg", "ml", "L", "N", "U", "m", "cm"),
        fontHeightRules = listOf(
            FontHeightRule(0.0, 50.0, 1.0, "Net wt up to 50g"),
            FontHeightRule(50.0, 200.0, 2.0, "Net wt 50g to 200g"),
            FontHeightRule(200.0, 1000.0, 4.0, "Net wt 200g to 1000g / 1kg"),
            FontHeightRule(1000.0, 50000.0, 6.0, "Net wt above 1kg / 1L")
        ),
        regulations = listOf(
            RegulationRule(
                ruleId = "PCR-R6-1A",
                name = "Manufacturer / Packer / Importer Identity",
                section = "Rule 6(1)(a)",
                description = "Name and complete physical address of manufacturer, packer, or importer must be clearly declared.",
                severity = "CRITICAL",
                statutoryReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(a)",
                penaltyClause = "Section 36(1) LM Act: Fine up to ₹25,000 for 1st offense, ₹50,000 for 2nd offense."
            ),
            RegulationRule(
                ruleId = "PCR-R6-1B",
                name = "Common / Generic Name of Commodity",
                section = "Rule 6(1)(b)",
                description = "Generic name of the packaged food or commodity must be prominently declared on Principal Display Panel.",
                severity = "MAJOR",
                statutoryReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(b)",
                penaltyClause = "Section 36(1) LM Act: Compounding penalty under Rule 32."
            ),
            RegulationRule(
                ruleId = "PCR-R6-1C",
                name = "Net Quantity in Standard SI Units",
                section = "Rule 6(1)(c)",
                description = "Net quantity must be declared in standard SI metric units (g, kg, ml, l) without misleading prefixes.",
                severity = "CRITICAL",
                statutoryReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(c)",
                penaltyClause = "Section 36(1) LM Act: Short quantity constitutes serious economic offense."
            ),
            RegulationRule(
                ruleId = "PCR-R6-1DA",
                name = "MRP Inclusive of All Taxes",
                section = "Rule 6(1)(da)",
                description = "Maximum Retail Price must explicitly include the words '(inclusive of all taxes)' or 'incl. of all taxes'.",
                severity = "CRITICAL",
                statutoryReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(da)",
                penaltyClause = "Overcharging or non-declaration attracts Section 36(1) penalty up to ₹1,00,000 / imprisonment."
            ),
            RegulationRule(
                ruleId = "PCR-R6-1E",
                name = "Unit Sale Price (USP)",
                section = "Rule 6(1)(e)",
                description = "Mandatory Unit Sale Price per g/kg/ml/litre/number to enable price transparency and consumer comparison.",
                severity = "MAJOR",
                statutoryReference = "Legal Metrology (Packaged Commodities) Amendment Rules, 2022 - Rule 6(1)(e)",
                penaltyClause = "Statutory notice under Rule 32 for missing or miscalculated unit price."
            ),
            RegulationRule(
                ruleId = "PCR-R6-1D",
                name = "Month & Year of Manufacture / Packing",
                section = "Rule 6(1)(d)",
                description = "Month and year of manufacture, packing, or import must be printed clearly.",
                severity = "MAJOR",
                statutoryReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(d)",
                penaltyClause = "Section 36(1) non-declaration penalty."
            ),
            RegulationRule(
                ruleId = "PCR-R6-1G",
                name = "Consumer Grievance Redressal Mechanism",
                section = "Rule 6(1)(g)",
                description = "Must provide name, address, telephone number, and email of consumer care executive.",
                severity = "MAJOR",
                statutoryReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(g)",
                penaltyClause = "Mandatory redressal mechanism breach."
            ),
            RegulationRule(
                ruleId = "PCR-R9",
                name = "Schedule II Minimum Font Height",
                section = "Rule 9 & Schedule II",
                description = "Height of letters and numerals on Principal Display Panel must meet minimum millimeter thresholds based on net weight.",
                severity = "MAJOR",
                statutoryReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Schedule II",
                penaltyClause = "Notice for non-conforming typography and reduced consumer readability."
            ),
            RegulationRule(
                ruleId = "FSSAI-R2020",
                name = "FSSAI License & Allergen Declarations",
                section = "FSSAI Reg 2020",
                description = "14-digit FSSAI license number, Veg/Non-Veg logo, ingredients in descending order, and allergen warnings.",
                severity = "CRITICAL",
                statutoryReference = "Food Safety and Standards (Packaging and Labelling) Regulations, 2020",
                penaltyClause = "FSSAI Act Section 52: Penalty for misbranded food up to ₹3,00,000."
            )
        )
    )

    // 2. Universal / Demo Global Baseline
    val DEMO_GLOBAL = RegulatoryRuleset(
        rulesetId = "DEMO-GLOBAL-v1",
        countryCode = "GLOBAL",
        countryName = "Global Baseline",
        flagEmoji = "🌐",
        currency = "USD",
        currencySymbol = "$",
        languages = listOf("en-US", "es-ES", "fr-FR"),
        isDefault = false,
        description = "Codex Alimentarius Universal Packaging Standard (CXS 1-1985)",
        consumerHelpline = "Global Consumer Safety Desk",
        standardUnits = listOf("g", "kg", "oz", "lb", "ml", "L", "fl oz"),
        fontHeightRules = listOf(
            FontHeightRule(0.0, 100.0, 1.2, "Small packages up to 100g"),
            FontHeightRule(100.0, 1000.0, 2.5, "Medium packages 100g to 1kg"),
            FontHeightRule(1000.0, 50000.0, 4.5, "Large packages over 1kg")
        ),
        regulations = listOf(
            RegulationRule(
                ruleId = "CODEX-SEC-4.1",
                name = "Name of Food & Nature",
                section = "Section 4.1",
                description = "The name shall indicate the true nature of the food and not be misleading.",
                severity = "CRITICAL",
                statutoryReference = "Codex General Standard for Labelling of Prepackaged Foods - CXS 1-1985 Section 4.1"
            ),
            RegulationRule(
                ruleId = "CODEX-SEC-4.2",
                name = "List of Ingredients & Allergens",
                section = "Section 4.2",
                description = "Complete list of ingredients in descending order of incoming weight (m/m) with highlighted allergens.",
                severity = "CRITICAL",
                statutoryReference = "Codex Alimentarius CXS 1-1985 Section 4.2"
            ),
            RegulationRule(
                ruleId = "CODEX-SEC-4.3",
                name = "Net Contents & Drained Weight",
                section = "Section 4.3",
                description = "Net contents declared in metric system or dual customary units.",
                severity = "MAJOR",
                statutoryReference = "Codex Alimentarius CXS 1-1985 Section 4.3"
            ),
            RegulationRule(
                ruleId = "CODEX-SEC-4.4",
                name = "Manufacturer / Distributor Contact",
                section = "Section 4.4",
                description = "Name and address of manufacturer, packer, distributor, or exporter.",
                severity = "MAJOR",
                statutoryReference = "Codex Alimentarius CXS 1-1985 Section 4.4"
            ),
            RegulationRule(
                ruleId = "CODEX-SEC-4.5",
                name = "Country of Origin",
                section = "Section 4.5",
                description = "Country of origin must be declared if its omission would mislead or deceive the consumer.",
                severity = "MAJOR",
                statutoryReference = "Codex Alimentarius CXS 1-1985 Section 4.5"
            ),
            RegulationRule(
                ruleId = "CODEX-SEC-4.7",
                name = "Date Marking & Storage Instructions",
                section = "Section 4.7",
                description = "Clear Best Before / Expiry date and any special storage conditions.",
                severity = "CRITICAL",
                statutoryReference = "Codex Alimentarius CXS 1-1985 Section 4.7"
            )
        )
    )

    // 3. European Union - EU FIC Regulation 1169/2011
    val EU_FIC_1169 = RegulatoryRuleset(
        rulesetId = "EU-FIC-1169-2011",
        countryCode = "EU",
        countryName = "European Union",
        flagEmoji = "🇪🇺",
        currency = "EUR",
        currencySymbol = "€",
        languages = listOf("en-GB", "de-DE", "fr-FR", "es-ES", "it-IT"),
        isDefault = false,
        description = "EU Regulation (EU) No 1169/2011 on Food Information to Consumers (FIC)",
        consumerHelpline = "EU Consumer Safety Network",
        standardUnits = listOf("g", "kg", "ml", "cl", "L"),
        fontHeightRules = listOf(
            FontHeightRule(0.0, 80.0, 0.9, "Small pack surface < 80cm² (x-height >= 0.9mm)"),
            FontHeightRule(80.0, 50000.0, 1.2, "Standard pack surface (x-height >= 1.2mm)")
        ),
        regulations = listOf(
            RegulationRule(
                ruleId = "EU-FIC-ART-9-1A",
                name = "Mandatory Food Name",
                section = "Article 9(1)(a)",
                description = "Legal name of the food or customary descriptive name.",
                severity = "CRITICAL",
                statutoryReference = "Regulation (EU) No 1169/2011 Art 9(1)(a)"
            ),
            RegulationRule(
                ruleId = "EU-FIC-ART-9-1B",
                name = "Ingredients & QUID Declarations",
                section = "Article 9(1)(b)",
                description = "List of ingredients with Quantitative Ingredient Declaration (QUID) for key highlighted ingredients.",
                severity = "MAJOR",
                statutoryReference = "Regulation (EU) No 1169/2011 Art 9(1)(b) & Art 22"
            ),
            RegulationRule(
                ruleId = "EU-FIC-ART-9-1C",
                name = "14 Major Allergens Emphasis",
                section = "Article 9(1)(c)",
                description = "Allergens must be emphasized in typography (e.g. bold, distinct background color).",
                severity = "CRITICAL",
                statutoryReference = "Regulation (EU) No 1169/2011 Annex II"
            ),
            RegulationRule(
                ruleId = "EU-FIC-ART-9-1E",
                name = "Net Quantity in Metric Units",
                section = "Article 9(1)(e)",
                description = "Net quantity declared in liters, centiliters, milliliters, kilograms, or grams.",
                severity = "MAJOR",
                statutoryReference = "Regulation (EU) No 1169/2011 Art 9(1)(e)"
            ),
            RegulationRule(
                ruleId = "EU-FIC-ART-9-1F",
                name = "Date of Minimum Durability / 'Use By'",
                section = "Article 9(1)(f)",
                description = "Best before or Use by date followed by required storage conditions.",
                severity = "CRITICAL",
                statutoryReference = "Regulation (EU) No 1169/2011 Art 24"
            ),
            RegulationRule(
                ruleId = "EU-FIC-ART-9-1L",
                name = "Mandatory Nutrition Declaration",
                section = "Article 9(1)(l)",
                description = "Energy value and amounts of fat, saturates, carbohydrate, sugars, protein, and salt per 100g/100ml.",
                severity = "MAJOR",
                statutoryReference = "Regulation (EU) No 1169/2011 Art 30"
            )
        )
    )

    // 4. United States - FDA 21 CFR 101
    val US_FDA_101 = RegulatoryRuleset(
        rulesetId = "US-FDA-LABEL-v1",
        countryCode = "US",
        countryName = "United States",
        flagEmoji = "🇺🇸",
        currency = "USD",
        currencySymbol = "$",
        languages = listOf("en-US", "es-US"),
        isDefault = false,
        description = "US FDA Food Labeling Guide (21 CFR Part 101) & Fair Packaging and Labeling Act (FPLA)",
        consumerHelpline = "FDA Consumer Inquiry Line: 1-888-INFO-FDA",
        standardUnits = listOf("oz", "fl oz", "lb", "g", "kg", "ml", "L"),
        fontHeightRules = listOf(
            FontHeightRule(0.0, 100.0, 1.6, "PDP area <= 5 sq in (1/16 in min font)"),
            FontHeightRule(100.0, 1000.0, 3.2, "PDP area 5 to 25 sq in (1/8 in min font)"),
            FontHeightRule(1000.0, 50000.0, 4.8, "PDP area > 25 sq in (3/16 in min font)")
        ),
        regulations = listOf(
            RegulationRule(
                ruleId = "FDA-21CFR-101.3",
                name = "Statement of Identity",
                section = "21 CFR 101.3",
                description = "Principal Display Panel must prominently state the common or usual name of the food.",
                severity = "CRITICAL",
                statutoryReference = "21 CFR 101.3"
            ),
            RegulationRule(
                ruleId = "FDA-21CFR-101.105",
                name = "Net Quantity Dual Declaration",
                section = "21 CFR 101.105",
                description = "Dual declaration in both US Customary units (oz/fl oz/lb) and Metric units (g/ml).",
                severity = "CRITICAL",
                statutoryReference = "Fair Packaging and Labeling Act & 21 CFR 101.105"
            ),
            RegulationRule(
                ruleId = "FDA-21CFR-101.9",
                name = "Nutrition Facts Panel",
                section = "21 CFR 101.9",
                description = "Standardized Nutrition Facts label with serving size, calories in bold, and daily value percentages.",
                severity = "CRITICAL",
                statutoryReference = "21 CFR 101.9"
            ),
            RegulationRule(
                ruleId = "FDA-FALCPA-2004",
                name = "Major Food Allergen Labeling (FALCPA / FASTER Act)",
                section = "21 U.S.C. 343(w)",
                description = "Clear declaration of 9 major food allergens (Milk, Eggs, Peanuts, Tree nuts, Fish, Crustacean shellfish, Wheat, Soy, Sesame).",
                severity = "CRITICAL",
                statutoryReference = "Food Allergen Labeling and Consumer Protection Act"
            ),
            RegulationRule(
                ruleId = "FDA-21CFR-101.5",
                name = "Name and Place of Business of Manufacturer / Packer / Distributor",
                section = "21 CFR 101.5",
                description = "Corporate name, street address, city, state, and ZIP code.",
                severity = "MAJOR",
                statutoryReference = "21 CFR 101.5"
            )
        )
    )

    val ALL_RULESETS = listOf(
        INDIA_PCR_2011,
        DEMO_GLOBAL,
        EU_FIC_1169,
        US_FDA_101
    )

    fun getRuleset(rulesetId: String?): RegulatoryRuleset {
        if (rulesetId.isNullOrBlank()) return INDIA_PCR_2011
        return ALL_RULESETS.firstOrNull { it.rulesetId.equals(rulesetId, ignoreCase = true) }
            ?: INDIA_PCR_2011
    }
}
