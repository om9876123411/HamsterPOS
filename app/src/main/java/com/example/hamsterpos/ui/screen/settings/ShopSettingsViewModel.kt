package com.example.hamsterpos.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamsterpos.data.dao.ShopInfoDao
import com.example.hamsterpos.data.model.ShopInfo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopSettingsViewModel(private val shopInfoDao: ShopInfoDao) : ViewModel() {

    // ดึงข้อมูลร้านค้าปัจจุบันมาแสดง
    val shopInfo: StateFlow<ShopInfo?> = shopInfoDao.getShopInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // ฟังก์ชันสำหรับอัปเดตข้อมูลร้านค้า
    fun updateShopInfo(shopName: String, promptPayId: String) {
        viewModelScope.launch {
            // เราใช้ id = 1 เสมอ เพราะมีร้านค้าได้แค่ร้านเดียว
            val updatedShopInfo = ShopInfo(id = 1, shopName = shopName, promptPayId = promptPayId)
            shopInfoDao.insertOrUpdate(updatedShopInfo)
        }
    }
}
