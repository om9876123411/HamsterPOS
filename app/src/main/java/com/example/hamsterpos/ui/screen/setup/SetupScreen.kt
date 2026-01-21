// Path: app/src/main/java/com/example/hamsterpos/ui/screen/setup/SetupScreen.kt
package com.example.hamsterpos.ui.screen.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hamsterpos.ui.theme.HamsterPOSTheme
import com.example.hamsterpos.ui.theme.LocalAppDimens

@Composable
fun SetupScreen(
    onNavigateToMain: () -> Unit,
    viewModel: SetupViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var shopName by remember { mutableStateOf("") }
    var promptPayInfo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ยินดีต้อนรับสู่\nHamster POS",
            // [FIXED] ใช้ขนาดจาก Dimens ที่เลือกให้
            fontSize = LocalAppDimens.current.welcomeTitle,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp, // เพิ่มระยะห่างระหว่างบรรทัด
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "มาตั้งค่าร้านของคุณกัน!",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // ... ส่วนที่เหลือของ UI เหมือนเดิม ...
        OutlinedTextField(
            value = shopName,
            onValueChange = { shopName = it },
            label = { Text("ชื่อร้านของคุณ (ไม่บังคับ)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = promptPayInfo,
            onValueChange = { promptPayInfo = it },
            label = { Text("ข้อมูล PromptPay (เบอร์มือถือ/เลขบัตร)") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "ข้อมูลนี้จะใช้สร้าง QR Code เท่านั้น",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp).fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = {
                if (promptPayInfo.isNotBlank()) {
                    viewModel.saveShopInfo(shopName, promptPayInfo)
                    onNavigateToMain()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = promptPayInfo.isNotBlank()
        ) {
            Text("บันทึกและเริ่มต้นใช้งาน", fontSize = 18.sp)
        }
    }
}