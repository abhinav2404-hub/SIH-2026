package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metrology_rules")
data class MetrologyRuleEntity(
    @PrimaryKey
    val ruleId: String, // e.g. "RULE_6_1_A", "RULE_6_1_E", "RULE_8", "RULE_9_SCH_II"
    val ruleNumber: String, // e.g. "Rule 6(1)(a)"
    val title: String,
    val description: String,
    val category: String, // "MANDATORY_DECLARATIONS", "UNIT_SALE_PRICE", "PDP_SPECIFICATIONS", "FONT_HEIGHT_SCHEDULE_II", "AGRI_COMMODITY_STANDARDS", "PENAL_PROVISIONS"
    val statutorySection: String, // e.g. "Section 18 & 36, Legal Metrology Act 2009"
    val mandatoryCheck: String, // Specific validation requirement
    val standardThreshold: String, // Expected standard
    val penaltyClause: String,
    val isAgriSpecific: Boolean = false,
    val guidanceNotes: String = ""
)
