package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "violations",
    foreignKeys = [
        ForeignKey(
            entity = ScanEntity::class,
            parentColumns = ["scanId"],
            childColumns = ["scanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["scanId"]),
        Index(value = ["ruleId"]),
        Index(value = ["severity"])
    ]
)
data class ViolationEntity(
    @PrimaryKey
    val violationId: String,
    val scanId: String,
    val ruleId: String,
    val ruleName: String,
    val severity: String = "MAJOR", // CRITICAL, MAJOR, MINOR, WARNING
    val detectedValue: String = "",
    val requiredValue: String = "",
    val evidenceSnippet: String = "",
    val reason: String = "",
    val penaltyClause: String = "",
    val status: String = "CONFIRMED" // CONFIRMED, DISMISSED, UNDER_APPEAL
)
