package com.example.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Composite relational POJO joining ScanEntity with its associated list of ViolationEntities.
 * Handles the one-to-many relationship between a Scan and its detected Violations.
 */
data class ScanWithViolations(
    @Embedded
    val scan: ScanEntity,

    @Relation(
        parentColumn = "scanId",
        entityColumn = "scanId"
    )
    val violations: List<ViolationEntity> = emptyList()
)
