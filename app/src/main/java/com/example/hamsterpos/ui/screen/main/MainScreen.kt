// 1. สร้างไฟล์ใหม่ตาม Path นี้
// Path: app/src/main/java/com/example/hamsterpos/ui/screen/main/MainScreen.kt
package com.example.hamsterpos.ui.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlignVerticalBottom
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hamsterpos.ui.screen.sales.SalesScreen
import com.example.hamsterpos.ui.screen.settings.AboutScreen
import com.example.hamsterpos.ui.screen.settings.ExpenseDetailScreen
import com.example.hamsterpos.ui.screen.settings.ExpenseScreen
import com.example.hamsterpos.ui.screen.settings.ProductManagementScreen
import com.example.hamsterpos.ui.screen.settings.SettingsScreen
import com.example.hamsterpos.ui.screen.settings.ShopSettingsScreen
import com.example.hamsterpos.ui.screen.summary.SummaryScreen
import com.example.hamsterpos.ui.screen.summary.TransactionDetailScreen
import com.example.hamsterpos.ui.screen.summary.TransactionHistoryScreen

// Sealed class สำหรับหน้าจอภายใน MainScreen เพื่อความชัดเจน
sealed class MainScreenTab(val route: String, val label: String, val icon: ImageVector) {
    object Sales : MainScreenTab("sales", "ขายของ", Icons.Default.Home)
    object Summary : MainScreenTab("summary", "สรุปยอด", Icons.Outlined.Summarize)
    object Settings : MainScreenTab("settings", "ตั้งค่า", Icons.Default.Settings)
    object ProductManagement : MainScreenTab("product_management", "จัดการสินค้า", Icons.Default.Edit) // Icon ไม่ได้ใช้ แต่ต้องมี
    object TransactionHistory : MainScreenTab("transaction_history", "ประวัติ", Icons.Default.History) // Icon ไม่ได้ใช้
    object TransactionDetail : MainScreenTab("transaction_detail/{transactionId}", "รายละเอียด", Icons.Default.AlignVerticalBottom)
    object ExpenseManagement : MainScreenTab("expense_management", "รายจ่าย", Icons.Default.Sell)
    object ExpenseDetail : MainScreenTab("expense_detail/{expenseId}", "รายละเอียดรายจ่าย", Icons.Default.Feed)
    object ShopSettings : MainScreenTab("shop_settings", "ตั้งค่าร้านค้า", Icons.Default.AllInbox)
    object About : MainScreenTab("about_screen", "เกี่ยวกับแอป", Icons.Default.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomBarItems = listOf(
        MainScreenTab.Sales,
        MainScreenTab.Summary,
        MainScreenTab.Settings,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomBarItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = MainScreenTab.Sales.route, Modifier.padding(innerPadding)) {
            composable(MainScreenTab.Sales.route) { SalesScreen() }
            composable(MainScreenTab.Summary.route) { SummaryScreen(navController = navController) }
            composable(MainScreenTab.Settings.route) {
                // [EDITED] ส่ง NavController ไปให้ SettingsScreen
                SettingsScreen(navController = navController)
            }
            // [NEW] เพิ่มหน้าใหม่เข้ามาใน NavHost
            composable(MainScreenTab.ProductManagement.route) {
                ProductManagementScreen(navController = navController)
            }
            // [NEW] เพิ่มหน้าใหม่เข้ามาใน NavHost
            composable(MainScreenTab.TransactionHistory.route) {
                TransactionHistoryScreen(navController = navController)
            }
            // [NEW] เพิ่มหน้าใหม่เข้ามาใน NavHost
            composable(
                route = MainScreenTab.TransactionDetail.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
            ) {
                TransactionDetailScreen(navController = navController)
            }
            composable(MainScreenTab.ExpenseManagement.route) {
                ExpenseScreen(navController = navController)
            }
            composable(
                route = MainScreenTab.ExpenseDetail.route,
                arguments = listOf(navArgument("expenseId") { type = NavType.IntType })
            ) {
                ExpenseDetailScreen(navController = navController)
            }

            composable(MainScreenTab.ShopSettings.route) {
                ShopSettingsScreen(navController = navController)
            }

            composable(MainScreenTab.About.route) {
                AboutScreen(navController = navController)
            }
        }
    }
}