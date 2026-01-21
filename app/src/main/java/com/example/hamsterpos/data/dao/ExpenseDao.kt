package com.example.hamsterpos.data.dao

import androidx.room.*
import com.example.hamsterpos.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    // [NEW] เพิ่มฟังก์ชันสำหรับอัปเดตข้อมูล
    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    // [NEW] เพิ่มฟังก์ชันสำหรับดึงข้อมูลรายจ่ายแค่รายการเดียวด้วย ID
    @Query("SELECT * from expenses WHERE id = :id")
    fun getExpense(id: Int): Flow<Expense>

    @Query("SELECT SUM(amount) FROM expenses WHERE createdAt BETWEEN :startTime AND :endTime")
    fun getTotalExpensesForDay(startTime: Long, endTime: Long): Flow<Double?> // ใช้ Double? เพราะอาจจะไม่มีรายจ่าย (NULL)
}
