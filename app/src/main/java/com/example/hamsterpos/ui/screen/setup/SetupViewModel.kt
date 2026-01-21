package com.example.hamsterpos.ui.screen.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hamsterpos.data.dao.ShopInfoDao
import com.example.hamsterpos.data.model.ShopInfo
import kotlinx.coroutines.launch

class SetupViewModel(private val shopInfoDao: ShopInfoDao) : ViewModel() {

    fun saveShopInfo(shopName: String, promptPayId: String) {
        // ทำงานใน background thread เพื่อไม่ให้กระทบกับ UI
        viewModelScope.launch {
            val shopInfo = ShopInfo(shopName = shopName, promptPayId = promptPayId)
            shopInfoDao.insertOrUpdate(shopInfo)
        }
    }
}

/*
// Factory สำหรับสร้าง SetupViewModel พร้อมกับส่ง Dao เข้าไป
class SetupViewModelFactory(private val shopInfoDao: ShopInfoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SetupViewModel(shopInfoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/
