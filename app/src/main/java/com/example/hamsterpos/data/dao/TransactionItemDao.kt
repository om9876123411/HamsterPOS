package com.example.hamsterpos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hamsterpos.data.model.TransactionItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionItemDao {
    @Insert
    suspend fun insertAll(items: List<TransactionItem>)

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    fun getItemsForTransaction(transactionId: Long): Flow<List<TransactionItem>>
}
