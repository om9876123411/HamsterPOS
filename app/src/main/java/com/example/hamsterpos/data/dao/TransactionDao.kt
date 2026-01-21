package com.example.hamsterpos.data.dao

import androidx.room.*
import com.example.hamsterpos.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // [แก้ไข] เปลี่ยนชื่อและประเภทการคืนค่าของฟังก์ชันนี้
    @Insert
    suspend fun insertAndGetId(transaction: Transaction): Long

    @Query("SELECT * FROM transactions WHERE createdAt BETWEEN :startTime AND :endTime ORDER BY createdAt DESC")
    fun getTransactionsForDay(startTime: Long, endTime: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<Transaction>>
}