package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [Index(value = ["barcode"], unique = false)]
)
data class ProductEntity(
    @PrimaryKey
    val productId: String,
    val barcode: String = "",
    val brand: String = "",
    val name: String = "",
    val category: String = "",
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
