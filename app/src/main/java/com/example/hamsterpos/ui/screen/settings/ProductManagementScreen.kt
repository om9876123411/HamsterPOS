package com.example.hamsterpos.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamsterpos.data.model.Product
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    navController: androidx.navigation.NavController,
    viewModel: ProductManagementViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val products by viewModel.allProducts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("จัดการสินค้าขายเร็ว") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                productToEdit = null // Ensure we're adding a new product
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, "Add Product")
            }
        }
    ) { paddingValues ->
        if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("ยังไม่มีสินค้า\nกดปุ่ม + เพื่อเพิ่มสินค้าชิ้นแรก", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductListItem(
                        product = product,
                        onEdit = {
                            // [FIXED] Use the correct 'product' variable
                            productToEdit = product
                            showDialog = true
                        },
                        onDelete = {
                            // [FIXED] Use the correct 'product' variable
                            viewModel.deleteProduct(product)
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddEditProductDialog(
            product = productToEdit,
            onDismiss = { showDialog = false },
            onConfirm = { name, price ->
                if (productToEdit == null) {
                    viewModel.addProduct(name, price)
                } else {
                    viewModel.updateProduct(productToEdit!!.copy(name = name, price = price))
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun ProductListItem(product: Product, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${NumberFormat.getCurrencyInstance(Locale("th", "TH")).format(product.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddEditProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "เพิ่มสินค้าใหม่" else "แก้ไขสินค้า") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ชื่อสินค้า") },
                    isError = name.isBlank() && isError
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("ราคา") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = price.isBlank() && isError
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val priceDouble = price.toDoubleOrNull()
                if (name.isNotBlank() && priceDouble != null) {
                    onConfirm(name, priceDouble)
                } else {
                    isError = true
                }
            }) {
                Text("บันทึก")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก")
            }
        }
    )
}
