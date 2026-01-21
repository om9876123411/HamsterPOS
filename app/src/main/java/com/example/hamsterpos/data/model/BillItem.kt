package com.example.hamsterpos.data.model

data class BillItem(
    val id: Int,
    val name: String,
    val pricePerUnit: Double,
    var quantity: Int
)