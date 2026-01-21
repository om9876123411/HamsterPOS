package com.example.hamsterpos.ui.screen.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NotificationPermissionScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "เปิดใช้งานการยืนยันอัตโนมัติ",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Hamster POS ต้องการสิทธิ์ในการเข้าถึงการแจ้งเตือนเพื่อตรวจสอบยอดเงินโอนเข้าจากแอปธนาคารของคุณโดยอัตโนมัติ",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = {
            // เปิดหน้าตั้งค่าของระบบเพื่อให้ผู้ใช้เปิดสิทธิ์
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            context.startActivity(intent)
        }) {
            Text("ไปที่หน้าตั้งค่า")
        }
    }
}
