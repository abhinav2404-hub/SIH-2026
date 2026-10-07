package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

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
