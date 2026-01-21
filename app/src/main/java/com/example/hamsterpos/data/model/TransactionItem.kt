package com.example.hamsterpos.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index // [เพิ่ม] import นี้
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_items",
    // [แก้ไข] เพิ่ม Index ที่นี่เพื่อแก้ปัญหา Warning และเพิ่มประสิทธิภาพ
    indices = [Index(value = ["transactionId"])],
    foreignKeys = [
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val transactionId: Long,
    val productName: String,
    val pricePerUnit: Double,
    val quantity: Int
)

