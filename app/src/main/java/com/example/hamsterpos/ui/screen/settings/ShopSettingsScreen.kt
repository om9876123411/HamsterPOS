package com.example.hamsterpos.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopSettingsScreen(
    navController: NavController,
    viewModel: ShopSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val currentShopInfo by viewModel.shopInfo.collectAsState()

    // State สำหรับเก็บค่าใน TextField
    var shopName by remember { mutableStateOf("") }
    var promptPayId by remember { mutableStateOf("") }

    // เมื่อข้อมูลร้านค้าจากฐานข้อมูลโหลดเสร็จครั้งแรก ให้ตั้งค่าใน TextField
    LaunchedEffect(currentShopInfo) {
        currentShopInfo?.let {
            shopName = it.shopName
            promptPayId = it.promptPayId
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ตั้งค่าร้านค้า") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (promptPayId.isNotBlank()) {
                    viewModel.updateShopInfo(shopName, promptPayId)
                    navController.navigateUp() // บันทึกแล้วกลับไปหน้าก่อนหน้า
                }
            }) {
                Icon(Icons.Filled.Check, "Save Settings")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text("ชื่อร้านของคุณ") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = promptPayId,
                onValueChange = { promptPayId = it },
                label = { Text("ข้อมูล PromptPay (เบอร์มือถือ/เลขบัตร)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}