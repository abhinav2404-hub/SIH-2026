package com.example.data.ruleset

/**
 * Defines a regulatory rule within a country's legal metrology / packaging standard.
 */
data class RegulationRule(
    val ruleId: String,
    val name: String,
    val section: String,
    val description: String,
    val severity: String = "MAJOR", // CRITICAL, MAJOR, MINOR
    val statutoryReference: String,
    val penaltyClause: String = "Statutory violation notice under applicable law."
)

/**
 * Defines minimum font height rules by package net weight/volume thresholds.
 */
data class FontHeightRule(
    val minNetQuantityGrams: Double,
    val maxNetQuantityGrams: Double,
    val requiredFontHeightMm: Double,
    val areaDescription: String
)

/**
 * Configuration-driven Regulatory Ruleset.
 * Can represent India (IN-PCR2011-v2011), Global Baseline (DEMO-GLOBAL-v1), EU (EU-FIC-1169-2011), or US (US-FDA-LABEL-v1).
 */
data class RegulatoryRuleset(
    val rulesetId: String,
    val countryCode: String,
    val countryName: String,
    val flagEmoji: String,
    val currency: String,
    val currencySymbol: String,
    val languages: List<String>,
    val regulations: List<RegulationRule>,
    val consumerHelpline: String,
    val standardUnits: List<String>,
    val fontHeightRules: List<FontHeightRule>,
    val isDefault: Boolean = false,
    val description: String = ""
)
