package com.example.hamsterpos.ui.screen.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamsterpos.data.dao.ExpenseDao
import com.example.hamsterpos.data.dao.TransactionDao
import com.example.hamsterpos.data.model.Transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

// Data class สำหรับเก็บสถานะของ UI หน้าสรุปยอด
data class SummaryUiState(
    val totalSales: Double = 0.0,
    val cashSales: Double = 0.0,
    val qrSales: Double = 0.0,
    val totalExpenses: Double = 0.0, // สำหรับอนาคต
    val recentTransactions: List<Transaction> = listOf()
)

class SummaryViewModel(
    transactionDao: TransactionDao,
    expenseDao: ExpenseDao // [NEW] เพิ่ม expenseDao
) : ViewModel() {

    // คำนวณเวลาเริ่มต้นและสิ้นสุดของวันนี้
    private val todayStart: Long
    private val todayEnd: Long

    init {
        val calendar = Calendar.getInstance()
        // ตั้งค่าเป็นเวลาเที่ยงคืนของวันนี้
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        todayStart = calendar.timeInMillis

        // ตั้งค่าเป็นเวลาสิ้นสุดของวันนี้
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        todayEnd = calendar.timeInMillis
    }

    // StateFlow ที่จะปล่อย SummaryUiState ออกไปให้ UI อัปเดตอัตโนมัติ
    val summaryUiState: StateFlow<SummaryUiState> =
        transactionDao.getTransactionsForDay(todayStart, todayEnd)
            .combine(expenseDao.getTotalExpensesForDay(todayStart, todayEnd)) { transactions, totalExpenses ->
                val total = transactions.sumOf { it.totalAmount }
                val cash = transactions.filter { it.paymentMethod == "เงินสด" }.sumOf { it.totalAmount }
                val qr = transactions.filter { it.paymentMethod == "สแกน QR" }.sumOf { it.totalAmount }

                SummaryUiState(
                    totalSales = total,
                    cashSales = cash,
                    qrSales = qr,
                    totalExpenses = totalExpenses ?: 0.0, // ถ้าไม่มีรายจ่าย ให้เป็น 0.0
                    recentTransactions = transactions.take(3)
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SummaryUiState()
            )
}
