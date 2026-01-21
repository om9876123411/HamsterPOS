// Path: app/src/main/java/com/example/hamsterpos/ui/screen/summary/TransactionHistoryScreen.kt
package com.example.hamsterpos.ui.screen.summary

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hamsterpos.data.model.Transaction
import com.example.hamsterpos.ui.screen.main.MainScreenTab
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    navController: NavController,
    viewModel: TransactionHistoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val transactions by viewModel.allTransactions.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val fileSaverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    coroutineScope.launch {
                        try {
                            val content = viewModel.generateCsvContent()
                            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
                            outputStream?.write(content.toByteArray())
                            outputStream?.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ประวัติการขายทั้งหมด") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "text/csv"
                            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                            putExtra(Intent.EXTRA_TITLE, "hamster_pos_export_$timestamp.csv")
                        }
                        fileSaverLauncher.launch(intent)
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Export to CSV")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("ยังไม่มีประวัติการขาย")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp)
            ) {
                items(transactions, key = { it.id }) { transaction ->
                    TransactionHistoryItem(
                        transaction = transaction,
                        onClick = {
                            // [CRITICAL FIX] แก้ไขวิธีการสร้าง Route ให้ถูกต้อง
                            // โดยการแทนที่ placeholder ด้วย ID จริงๆ
                            val route = MainScreenTab.TransactionDetail.route.replace(
                                "{transactionId}",
                                transaction.id.toString()
                            )
                            navController.navigate(route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionHistoryItem(transaction: Transaction, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("th", "TH")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ยอดขาย #${transaction.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(Date(transaction.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    currencyFormat.format(transaction.totalAmount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    transaction.paymentMethod,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
