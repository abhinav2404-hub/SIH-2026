package com.example.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Composite relational POJO joining ScanEntity with its optional ProductEntity
 * and associated list of ViolationEntities.
 */
data class ScanWithDetails(
    @Embedded
    val scan: ScanEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val product: ProductEntity?,

    @Relation(
        parentColumn = "scanId",
        entityColumn = "scanId"
    )
    val violations: List<ViolationEntity> = emptyList()
)
