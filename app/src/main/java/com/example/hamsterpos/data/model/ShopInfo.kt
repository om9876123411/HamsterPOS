package com.example.hamsterpos.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_info")
data class ShopInfo(
    @PrimaryKey val id: Int = 1, // มีข้อมูลร้านค้าได้แค่ร้านเดียว
    val shopName: String,
    val promptPayId: String
)