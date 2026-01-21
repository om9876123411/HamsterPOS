// Path: app/src/main/java/com/example/hamsterpos/ui/screen/settings/SettingsScreen.kt
package com.example.hamsterpos.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hamsterpos.ui.theme.HamsterPOSTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ตั้งค่า") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Text(
                    text = "การจัดการ",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.Inventory2,
                    title = "จัดการสินค้าขายเร็ว",
                    subtitle = "เพิ่ม/ลบ/แก้ไข สินค้าบนหน้าจอหลัก",
                    // [EDITED] เพิ่ม onClick
                    onClick = {
                        navController.navigate(com.example.hamsterpos.ui.screen.main.MainScreenTab.ProductManagement.route)
                    }
                )
            }
            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.ReceiptLong,
                    title = "บันทึกรายจ่าย",
                    subtitle = "บันทึกค่าใช้จ่ายต่างๆ ของร้าน",
                    onClick = {
                        navController.navigate(com.example.hamsterpos.ui.screen.main.MainScreenTab.ExpenseManagement.route)
                    }
                )
            }

            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

            item {
                Text(
                    text = "บัญชี",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.Store,
                    title = "ตั้งค่าร้านค้า",
                    subtitle = "เปลี่ยนชื่อร้านและข้อมูล PromptPay",
                    onClick = {
                        navController.navigate(com.example.hamsterpos.ui.screen.main.MainScreenTab.ShopSettings.route)
                    }
                )
            }
            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.WorkspacePremium,
                    title = "อัปเกรดเป็นพรีเมียม",
                    subtitle = "ปลดล็อกฟีเจอร์ทั้งหมด",
                    isPremium = true
                )
            }
            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.Info,
                    title = "เกี่ยวกับแอป",
                    subtitle = "เวอร์ชัน 1.0.0 (Prototype)",
                    onClick = {
                        navController.navigate(com.example.hamsterpos.ui.screen.main.MainScreenTab.About.route)
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isPremium: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(40.dp),
            tint = if(isPremium) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isPremium) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        } else {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


/*
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    HamsterPOSTheme {
        SettingsScreen()
    }
}
*/
