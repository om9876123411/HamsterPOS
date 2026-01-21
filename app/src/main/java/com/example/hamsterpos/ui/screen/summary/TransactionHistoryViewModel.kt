package com.example.hamsterpos.ui.screen.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamsterpos.data.dao.TransactionDao
import com.example.hamsterpos.data.model.Transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryViewModel(private val transactionDao: TransactionDao) : ViewModel() {
    val allTransactions: StateFlow<List<Transaction>> = transactionDao.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    // [NEW] ฟังก์ชันสำหรับสร้างเนื้อหาไฟล์ CSV
    suspend fun generateCsvContent(): String {
        val transactions = allTransactions.first() // ดึงข้อมูลล่าสุดจาก Flow
        val header = "ID,วันที่,เวลา,ยอดรวม,ช่องทางชำระเงิน\n"

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        // ใช้ StringBuilder เพื่อประสิทธิภาพในการต่อข้อความยาวๆ
        return buildString {
            append(header)
            transactions.forEach { transaction ->
                val date = Date(transaction.createdAt)
                append(transaction.id).append(",")
                append(dateFormat.format(date)).append(",")
                append(timeFormat.format(date)).append(",")
                append(transaction.totalAmount).append(",")
                append(transaction.paymentMethod).append("\n")
            }
        }
    }
}
