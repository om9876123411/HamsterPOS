package com.example.hamsterpos


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamsterpos.data.dao.ShopInfoDao
import com.example.hamsterpos.ui.navigation.AppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// สถานะของหน้าจอเริ่มต้น
sealed class StartUpState {
    object Loading : StartUpState()
    data class Complete(val startDestination: String) : StartUpState()
}

class MainActivityViewModel(shopInfoDao: ShopInfoDao) : ViewModel() {

    private val _startUpState = MutableStateFlow<StartUpState>(StartUpState.Loading)
    val startUpState = _startUpState.asStateFlow()

    init {
        viewModelScope.launch {
            // ดึงข้อมูลร้านค้าจากฐานข้อมูลแค่ครั้งเดียวตอนเริ่มต้น
            val shopInfo = shopInfoDao.getShopInfo().first()
            if (shopInfo == null) {
                // ถ้าไม่มีข้อมูล ให้ไปหน้าตั้งค่า
                _startUpState.value = StartUpState.Complete(AppScreen.Setup.route)
            } else {
                // ถ้ามีข้อมูลแล้ว ให้ไปหน้าหลัก
                _startUpState.value = StartUpState.Complete(AppScreen.Main.route)
            }
        }
    }
}
