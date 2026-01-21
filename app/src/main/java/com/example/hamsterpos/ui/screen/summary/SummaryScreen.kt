// Path: app/src/main/java/com/example/hamsterpos/ui/screen/summary/SummaryScreen.kt
package com.example.hamsterpos.ui.screen.summary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hamsterpos.data.model.Transaction
import com.example.hamsterpos.ui.screen.main.MainScreenTab
import com.example.hamsterpos.ui.theme.HamsterPOSTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    navController: NavController,
    viewModel: SummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // ดึงข้อมูล UiState จาก ViewModel ซึ่งจะอัปเดตอัตโนมัติ
    val uiState by viewModel.summaryUiState.collectAsState()

    // คำนวณกำไรเบื้องต้นจาก UiState
    val netProfit = uiState.totalSales - uiState.totalExpenses

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("สรุปยอดประจำวัน") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ส่วนของการ์ดสรุปยอด
            item {
                SummaryCards(
                    totalSales = uiState.totalSales,
                    cashSales = uiState.cashSales,
                    qrSales = uiState.qrSales,
                    expenses = uiState.totalExpenses,
                    profit = netProfit
                )
            }

            // ส่วนของประวัติการขายล่าสุด
            item {
                TransactionHistoryCard(
                    transactions = uiState.recentTransactions,
                    // [EDITED] ทำให้เมื่อกดการ์ดนี้ จะนำทางไปที่หน้าประวัติทั้งหมด
                    onClick = {
                        navController.navigate(MainScreenTab.TransactionHistory.route)
                    }
                )
            }
        }
    }
}

// Composable สำหรับแสดงการ์ดสรุปยอดต่างๆ
@Composable
private fun SummaryCards(totalSales: Double, cashSales: Double, qrSales: Double, expenses: Double, profit: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SummaryCard(title = "ยอดขายรวม", amount = totalSales, isPrimary = true)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(title = "💵 เงินสด", amount = cashSales, modifier = Modifier.weight(1f))
            SummaryCard(title = "📱 สแกน QR", amount = qrSales, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(title = "💸 รายจ่าย", amount = expenses, amountColor = Color(0xFFD32F2F), modifier = Modifier.weight(1f))
            SummaryCard(title = "💰 กำไรเบื้องต้น", amount = profit, amountColor = Color(0xFF388E3C), modifier = Modifier.weight(1f))
        }
    }
}

// Composable สำหรับการ์ดแต่ละใบ
@Composable
private fun SummaryCard(modifier: Modifier = Modifier, title: String, amount: Double, isPrimary: Boolean = false, amountColor: Color = MaterialTheme.colorScheme.primary) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("th", "TH")) }
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = title, style = if (isPrimary) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currencyFormat.format(amount), style = if (isPrimary) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = if (isPrimary) MaterialTheme.colorScheme.primary else amountColor)
        }
    }
}

// Composable สำหรับการ์ดแสดงประวัติการขายล่าสุด
@Composable
private fun TransactionHistoryCard(transactions: List<Transaction>, onClick: () -> Unit) {
    val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2; minimumFractionDigits = 2 } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // ทำให้การ์ดทั้งใบกดได้
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ประวัติการขายล่าสุด", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (transactions.isEmpty()) {
                Text("ยังไม่มีการขายสำหรับวันนี้", modifier = Modifier.padding(vertical = 8.dp))
            } else {
                transactions.forEach { transaction ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(transaction.createdAt)),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(transaction.paymentMethod, modifier = Modifier.weight(1f))
                        Text("${currencyFormat.format(transaction.totalAmount)} บาท", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SummaryScreenPreview() {
    HamsterPOSTheme {
        SummaryScreen(navController = androidx.navigation.compose.rememberNavController())
    }
}
