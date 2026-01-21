package com.example.hamsterpos.data.dao

import androidx.room.*
import com.example.hamsterpos.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // [NEW] เพิ่มฟังก์ชันสำหรับอัปเดตข้อมูลสินค้า
    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM products ORDER BY id ASC")
    fun getAllProducts(): Flow<List<Product>>
}
