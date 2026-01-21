package com.example.hamsterpos.ui.screen.sales

import androidx.lifecycle.*
import com.example.hamsterpos.data.dao.ProductDao
import com.example.hamsterpos.data.dao.ShopInfoDao
import com.example.hamsterpos.data.dao.TransactionDao
import com.example.hamsterpos.data.dao.TransactionItemDao
import com.example.hamsterpos.data.model.BillItem
import com.example.hamsterpos.data.model.Product
import com.example.hamsterpos.data.model.ShopInfo
import com.example.hamsterpos.data.model.Transaction
import com.example.hamsterpos.data.model.TransactionItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// [EDITED] เพิ่ม transactionItemDao เข้ามาใน Constructor
class SalesViewModel(
    private val productDao: ProductDao,
    private val transactionDao: TransactionDao,
    private val shopInfoDao: ShopInfoDao,
    private val transactionItemDao: TransactionItemDao // เพิ่ม Dao ใหม่
) : ViewModel() {

    val allProducts: StateFlow<List<Product>> = productDao.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    val shopInfo: StateFlow<ShopInfo?> = shopInfoDao.getShopInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // [EDITED] แก้ไขฟังก์ชัน completeSale ให้รับรายการสินค้าเข้ามา และบันทึก TransactionItem
    fun completeSale(billItems: List<BillItem>, paymentMethod: String) {
        if (billItems.isEmpty()) return

        val totalAmount = billItems.sumOf { it.pricePerUnit * it.quantity }

        viewModelScope.launch {
            // 1. สร้างและบันทึก Transaction หลัก และรับ ID ที่ถูกสร้างขึ้นมาใหม่
            val newTransaction = Transaction(totalAmount = totalAmount, paymentMethod = paymentMethod)
            // หมายเหตุ: เราต้องไปแก้ Dao ให้คืนค่า Long กลับมาด้วย
            val transactionId = transactionDao.insertAndGetId(newTransaction)

            // 2. แปลง BillItem เป็น TransactionItem
            val transactionItems = billItems.map { billItem ->
                TransactionItem(
                    transactionId = transactionId,
                    productName = billItem.name,
                    pricePerUnit = billItem.pricePerUnit,
                    quantity = billItem.quantity
                )
            }

            // 3. บันทึกรายการสินค้าทั้งหมด
            transactionItemDao.insertAll(transactionItems)
        }
    }
}


/*
// Factory สำหรับสร้าง SalesViewModel
class SalesViewModelFactory(
    private val productDao: ProductDao,
    private val transactionDao: TransactionDao,
    private val shopInfoDao: ShopInfoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SalesViewModel(productDao, transactionDao, shopInfoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/
