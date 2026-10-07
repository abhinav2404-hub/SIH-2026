package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scans",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["timestamp"]),
        Index(value = ["syncStatus"])
    ]
)
data class ScanEntity(
    @PrimaryKey
    val scanId: String,
    val productId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val rulesetId: String = "IN-PCR2011-v2011",
    val overallStatus: String = "PASS", // PASS, REVIEW, FAIL, NOT_VERIFIABLE
    val complianceScore: Int = 100,
    val rawOcrText: String = "",
    val imageUri: String = "",
    val inspectorBadge: String = "",
    val inspectorNotes: String = "",
    val syncStatus: String = "PENDING", // PENDING, SYNCED, FAILED
    val latencyMs: Long = 0L,
    val violationsCount: Int = 0
)
