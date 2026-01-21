package com.example.hamsterpos.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val totalAmount: Double,
    val paymentMethod: String, // "cash" or "qr"
    val createdAt: Long = System.currentTimeMillis()
)