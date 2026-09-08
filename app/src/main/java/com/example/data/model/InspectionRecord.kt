package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inspection_records")
data class InspectionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productName: String,
    val brandName: String,
    val category: String,
    val barcode: String = "",
    val netQuantity: String,
    val declaredMrp: String,
    val declaredUsp: String,
    val countryOfOrigin: String,
    val manufacturerAddress: String,
    val consumerCareContact: String,
    val mfgPackingDate: String,
    val pdpFontCompliance: Boolean,
    val complianceScore: Int,
    val overallStatus: String,
    val violationsCount: Int,
    val ruleViolationsSummary: String,
    val sampleId: String? = null,
    val inspectorName: String = "Legal Metrology Inspector",
    val inspectorBadge: String = "LMO-DL-2026-0842",
    val inspectionLocation: String = "Central Verification Unit, New Delhi",
    val officerNotes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val noticeGenerated: Boolean = false
)
