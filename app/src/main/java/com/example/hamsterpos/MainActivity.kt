package com.example.hamsterpos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.hamsterpos.ui.navigation.AppNavigation
import com.example.hamsterpos.ui.theme.HamsterPOSTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels { AppViewModelProvider.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HamsterPOSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val startUpState by viewModel.startUpState.collectAsState()
                    MainAppContent(startUpState)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(state: StartUpState) {
    when (state) {
        is StartUpState.Loading -> {
            // แสดงหน้าจอ Loading ขณะที่ ViewModel กำลังตรวจสอบข้อมูล
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is StartUpState.Complete -> {
            // เมื่อตรวจสอบเสร็จ ให้เริ่ม Navigation ไปยังหน้าจอที่ถูกต้อง
            AppNavigation(startDestination = state.startDestination)
        }
    }
}
