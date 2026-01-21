package com.example.hamsterpos

import android.app.Application
import com.example.hamsterpos.data.AppDatabase

class HamsterPOSApplication : Application() {
    // ใช้ lazy เพื่อให้ database ถูกสร้างขึ้นเมื่อมีการเรียกใช้ครั้งแรกเท่านั้น
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}