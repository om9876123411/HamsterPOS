
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hamsterpos.HamsterPOSApplication
import com.example.hamsterpos.MainActivityViewModel
import com.example.hamsterpos.ui.screen.sales.SalesViewModel
import com.example.hamsterpos.ui.screen.settings.ExpenseDetailViewModel
import com.example.hamsterpos.ui.screen.settings.ExpenseViewModel
import com.example.hamsterpos.ui.screen.settings.ProductManagementViewModel
import com.example.hamsterpos.ui.screen.settings.ShopSettingsViewModel
import com.example.hamsterpos.ui.screen.setup.SetupViewModel
import com.example.hamsterpos.ui.screen.summary.SummaryViewModel
import com.example.hamsterpos.ui.screen.summary.TransactionHistoryViewModel
import com.example.hamsterpos.ui.screen.summary.TransactionDetailViewModel // เพิ่ม import

/**
 * Provider Factory ส่วนกลางสำหรับสร้าง ViewModel ทั้งหมดในแอป
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer สำหรับ MainActivityViewModel (ที่เคยขาดไป)
        initializer {
            MainActivityViewModel(hamsterPOSApplication().database.shopInfoDao())
        }

        // Initializer สำหรับ SetupViewModel
        initializer {
            SetupViewModel(
                hamsterPOSApplication().database.shopInfoDao()
            )
        }

        // Initializer สำหรับ SalesViewModel
        initializer {
            SalesViewModel(
                hamsterPOSApplication().database.productDao(),
                hamsterPOSApplication().database.transactionDao(),
                hamsterPOSApplication().database.shopInfoDao(),
                hamsterPOSApplication().database.transactionItemDao()
            )
        }

        // Initializer สำหรับ ProductManagementViewModel
        initializer {
            ProductManagementViewModel(hamsterPOSApplication().database.productDao())
        }

        // [NEW] Initializer สำหรับ TransactionHistoryViewModel
        initializer {
            TransactionHistoryViewModel(hamsterPOSApplication().database.transactionDao())
        }

        // [NEW] Initializer สำหรับ SummaryViewModel
        initializer {
            SummaryViewModel(hamsterPOSApplication().database.transactionDao(),
                hamsterPOSApplication().database.expenseDao() // เพิ่ม
            )
        }

        // [NEW] Initializer สำหรับ TransactionDetailViewModel
        initializer {
            TransactionDetailViewModel(
                this.createSavedStateHandle(),
                hamsterPOSApplication().database.transactionItemDao()
            )
        }

        // [NEW] Initializer สำหรับ ExpenseViewModel
        initializer {
            ExpenseViewModel(hamsterPOSApplication().database.expenseDao())
        }

        // [NEW] Initializer สำหรับ ExpenseDetailViewModel
        initializer {
            ExpenseDetailViewModel(
                this.createSavedStateHandle(),
                hamsterPOSApplication().database.expenseDao()
            )
        }

        // [NEW] Initializer สำหรับ ShopSettingsViewModel
        initializer {
            ShopSettingsViewModel(hamsterPOSApplication().database.shopInfoDao())
        }
    }
}

/**
 * Extension function เพื่อให้เข้าถึง instance ของ Application ได้ง่ายขึ้น
 */
fun CreationExtras.hamsterPOSApplication(): HamsterPOSApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HamsterPOSApplication)

