package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room database entity representing a product / packaged commodity master record.
 */
@Entity(
    tableName = "products",
    indices = [
        Index(value = ["barcode"], unique = false),
        Index(value = ["name"], unique = false)
    ]
)
data class ProductEntity(
    @PrimaryKey
    val productId: String,
    val barcode: String = "",
    val brand: String = "",
    val name: String = "",
    val category: String = "General Packaged Commodity",
    val manufacturer: String = "",
    val countryOfOrigin: String = "India",
    val consumerCare: String = "",
    val netQuantityDeclared: String = "",
    val mrpDeclared: Double = 0.0,
    val currency: String = "INR",
    val ingredientsText: String = "",
    val allergensText: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
