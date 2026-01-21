package com.example.hamsterpos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hamsterpos.data.dao.ExpenseDao
import com.example.hamsterpos.data.dao.ProductDao
import com.example.hamsterpos.data.dao.ShopInfoDao
import com.example.hamsterpos.data.dao.TransactionDao
import com.example.hamsterpos.data.dao.TransactionItemDao
import com.example.hamsterpos.data.model.Expense
import com.example.hamsterpos.data.model.Product
import com.example.hamsterpos.data.model.ShopInfo
import com.example.hamsterpos.data.model.Transaction
import com.example.hamsterpos.data.model.TransactionItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// [แก้ไข] เพิ่ม TransactionItem::class และเปลี่ยน version เป็น 2
@Database(entities = [ShopInfo::class, Product::class, Transaction::class, Expense::class, TransactionItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shopInfoDao(): ShopInfoDao
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionItemDao(): TransactionItemDao // [แก้ไข] เพิ่ม abstract fun สำหรับ Dao ใหม่
    abstract fun expenseDao(): ExpenseDao // [เพิ่ม] บรรทัดนี้

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hamster_pos_database"
                )
                    // [แก้ไข] เพิ่มบรรทัดนี้เพื่อจัดการการอัปเกรดเวอร์ชันแบบง่ายๆ
                    // โดยการลบฐานข้อมูลเก่าทิ้งแล้วสร้างใหม่ทั้งหมด (เหมาะสำหรับช่วงพัฒนา)
                    .fallbackToDestructiveMigration()
                    // [NEW] เพิ่ม Callback เข้าไปตอนสร้างฐานข้อมูล
                    .addCallback(AppDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    // [NEW] สร้าง Callback class ด้านใน AppDatabase
    private class AppDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                // ใช้ Coroutine เพื่อทำงานใน background
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateProducts(database.productDao())
                }
            }
        }

        // ฟังก์ชันสำหรับเพิ่มข้อมูลสินค้าจำลอง
        suspend fun prepopulateProducts(productDao: ProductDao) {
            val dummyProducts = listOf(
                Product(name = "หมูปิ้ง", price = 10.0),
                Product(name = "ข้าวเหนียว", price = 5.0),
                Product(name = "น้ำดื่ม", price = 10.0),
                Product(name = "ลูกชิ้น", price = 10.0),
                Product(name = "โค้ก", price = 15.0),
                Product(name = "ชาเย็น", price = 25.0),
                Product(name = "ชุดสุดคุ้ม", price = 50.0)
            )

            dummyProducts.forEach { productDao.insertProduct(it) }
        }
    }
}