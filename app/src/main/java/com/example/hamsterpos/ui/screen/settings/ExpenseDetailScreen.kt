package com.example.hamsterpos.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    navController: NavController,
    viewModel: ExpenseDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val expense by viewModel.uiState.collectAsState()

    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    // เมื่อข้อมูล expense โหลดเสร็จครั้งแรก ให้ตั้งค่าใน TextField
    LaunchedEffect(expense) {
        if (expense.description.isNotEmpty()) {
            description = expense.description
            amount = expense.amount.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("แก้ไขรายจ่าย") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val amountDouble = amount.toDoubleOrNull()
                if (description.isNotBlank() && amountDouble != null) {
                    viewModel.updateExpense(description, amountDouble)
                    navController.navigateUp() // กลับไปหน้าก่อนหน้า
                }
            }) {
                Icon(Icons.Filled.Check, "Save Expense")
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
                value = description,
                onValueChange = { description = it },
                label = { Text("คำอธิบาย") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("จำนวนเงิน") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}