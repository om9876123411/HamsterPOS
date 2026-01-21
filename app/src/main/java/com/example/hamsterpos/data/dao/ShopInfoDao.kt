package com.example.hamsterpos.data.dao

import androidx.room.*
import com.example.hamsterpos.data.model.ShopInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopInfoDao {
    // ใช้ OnConflictStrategy.REPLACE เพราะเรามีข้อมูลร้านค้าแค่แถวเดียว
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(shopInfo: ShopInfo)

    @Query("SELECT * FROM shop_info WHERE id = 1")
    fun getShopInfo(): Flow<ShopInfo?>
}