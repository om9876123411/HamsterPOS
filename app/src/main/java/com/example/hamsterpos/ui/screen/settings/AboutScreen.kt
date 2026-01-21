package com.example.hamsterpos.ui.screen.settings


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hamsterpos.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("เกี่ยวกับแอป") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.hamster_mascot),
                contentDescription = "Hamster POS Mascot",
                modifier = Modifier.size(250.dp) // กำหนดขนาดรูปภาพ
            )
//            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Hamster POS",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "เวอร์ชัน 1.0.0 (Beta)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "แอปพลิเคชันคิดเงินที่ง่ายและรวดเร็วที่สุด\nออกแบบมาเพื่อร้านค้ารายย่อยโดยเฉพาะ",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            /*
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "พัฒนาโดย", // ใส่ชื่อของคุณตรงนี้ได้เลย
                style = MaterialTheme.typography.bodySmall
            )
             */
        }
    }
}
