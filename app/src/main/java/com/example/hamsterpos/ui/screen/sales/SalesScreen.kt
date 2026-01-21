// Path: app/src/main/java/com/example/hamsterpos/ui/screen/sales/SalesScreen.kt
package com.example.hamsterpos.ui.screen.sales

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamsterpos.data.model.BillItem
import com.example.hamsterpos.data.model.Product
import com.example.hamsterpos.services.PaymentNotificationListener
import com.example.hamsterpos.ui.theme.HamsterPOSTheme
import com.example.hamsterpos.ui.theme.LocalAppDimens
import com.example.hamsterpos.utils.crc16
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.*
import kotlin.random.Random

// --- ฟังก์ชันสำหรับสร้าง QR Code ---
fun generateQrBitmap(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bmp
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// --- Main Composable for the Sales Screen ---
@Composable
fun SalesScreen(
    viewModel: SalesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val billItems = remember { mutableStateListOf<BillItem>() }
    val showQrDialog = remember { mutableStateOf(false) }
    val totalAmount = remember { derivedStateOf { billItems.sumOf { it.pricePerUnit * it.quantity } } }

    val quickSaleProducts by viewModel.allProducts.collectAsState()
    val shopInfo by viewModel.shopInfo.collectAsState()
    val promptPayId = shopInfo?.promptPayId ?: ""

    fun updateItemQuantity(productName: String, change: Int) {
        val existingItem = billItems.find { it.name == productName }
        if (existingItem != null) {
            val newQuantity = existingItem.quantity + change
            if (newQuantity > 0) {
                val index = billItems.indexOf(existingItem)
                billItems[index] = existingItem.copy(quantity = newQuantity)
            } else {
                billItems.remove(existingItem)
            }
        }
    }

    fun addItemToBill(product: Product) {
        val existingItem = billItems.find { it.name == product.name }
        if (existingItem != null) {
            updateItemQuantity(product.name, 1)
        } else {
            billItems.add(BillItem(id = Random.nextInt(), name = product.name, pricePerUnit = product.price, quantity = 1))
        }
    }

    fun completeSale(paymentMethod: String) {
        viewModel.completeSale(billItems.toList(), paymentMethod)
        billItems.clear()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        // [FIXED] ปรับสัดส่วน weight ใหม่
        BillDisplay(
            modifier = Modifier.weight(1.4f), // เพิ่มพื้นที่ให้ส่วนแสดงบิล
            items = billItems,
            total = totalAmount.value,
            onClearBill = { billItems.clear() },
            onQuantityChange = { productName, change -> updateItemQuantity(productName, change) }
        )
        QuickItemsGrid(
            modifier = Modifier.weight(1f), // ลดพื้นที่ของตารางสินค้าลง
            products = quickSaleProducts,
            onProductClick = { product -> addItemToBill(product) }
        )
        ActionButtons(
            onCashClick = { if (billItems.isNotEmpty()) completeSale("เงินสด") },
            onQrClick = { if (billItems.isNotEmpty()) showQrDialog.value = true }
        )
    }

//    // [NEW] ใช้ LaunchedEffect เพื่อตรวจสอบการชำระเงินเป็นระยะ
//    LaunchedEffect(Unit) {
//        while (true) {
//            // ตรวจสอบว่ายอดเงินที่ได้รับตรงกับยอดที่รอหรือไม่
//            if (PaymentNotificationListener.lastReceivedAmount == totalAmount.value) {
//                PaymentNotificationListener.lastReceivedAmount = null // เคลียร์ค่า
//                showQrDialog.value = false
//                completeSale("สแกน QR")
//                break // ออกจาก Loop
//            }
//            delay(1500) // รอ 1 วินาทีแล้วเช็คใหม่
//        }
//    }

    if (showQrDialog.value) {
        val payload = remember(promptPayId, totalAmount.value) {
            if (promptPayId.isBlank()) return@remember ""
            fun f(id: String, value: String): String { return id + String.format("%02d", value.length) + value }
            val target = promptPayId.filter { it.isDigit() }
            val targetType = when (target.length) {
                13 -> "02"
                10 -> "01"
                else -> "03"
            }
            val formattedTarget = if (targetType == "01") { ("0000000000000" + "66" + target.substring(1)).takeLast(13) } else { target }
            val data = mutableListOf<String>()
            data.add(f("00", "01"))
            data.add(f("01", "12"))
            data.add(f("29", f("00", "A000000677010111") + f(targetType, formattedTarget)))
            data.add(f("58", "TH"))
            data.add(f("53", "764"))
            data.add(f("54", String.format(Locale.US, "%.2f", totalAmount.value)))
            val dataToCrc = data.joinToString("") + "6304"
            val crc = crc16(dataToCrc)
            dataToCrc + String.format(Locale.US, "%04X", crc).uppercase()
        }
        val qrBitmap = remember(payload) { if (payload.isNotBlank()) generateQrBitmap(payload) else null }
        QrCodeDialog(
            qrBitmap = qrBitmap,
            amount = totalAmount.value,
            onConfirm = {
                showQrDialog.value = false
                completeSale("สแกน QR")
            },
            onDismiss = { showQrDialog.value = false }
        )
    }
}

// --- Sub-Composable: QrCodeDialog ---
@Composable
fun QrCodeDialog(qrBitmap: Bitmap?, amount: Double, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("สแกนเพื่อจ่ายเงิน", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code for payment",
                        modifier = Modifier.size(220.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier.size(220.dp).background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("เกิดข้อผิดพลาด\nในการสร้าง QR Code", textAlign = TextAlign.Center)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ยอดชำระ: ${NumberFormat.getCurrencyInstance(Locale("th", "TH")).format(amount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("ยืนยันการชำระเงิน")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("ยกเลิก", color = Color.Gray)
                }
            }
        }
    }
}


// --- [REVISED] BillDisplay และ BillListItem ---
@Composable
fun BillDisplay(
    modifier: Modifier = Modifier,
    items: List<BillItem>,
    total: Double,
    onClearBill: () -> Unit,
    onQuantityChange: (productName: String, change: Int) -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("th", "TH")) }

    Card(
        modifier = modifier.fillMaxWidth().padding(12.dp),
        shape = RoundedCornerShape(24.dp), // ขอบมนขึ้น
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 24.dp)) {
                Text(
                    text = currencyFormat.format(total),
                    // [FIXED] ใช้ขนาดจาก Dimens
                    fontSize = LocalAppDimens.current.displayTotal,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(
                    onClick = onClearBill,
                    modifier = Modifier.align(Alignment.TopEnd),
                    enabled = items.isNotEmpty()
                ) {
                    Text("ล้างบิล")
                }
            }
            Divider(color = MaterialTheme.colorScheme.background)
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("เพิ่มสินค้าเพื่อเริ่มการขาย...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(items, key = { it.id }) { item ->
                        BillListItem(
                            item = item,
                            onIncrease = { onQuantityChange(item.name, 1) },
                            onDecrease = { onQuantityChange(item.name, -1) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BillListItem(item: BillItem, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold,
                // [FIXED] ใช้ขนาดจาก Dimens
                fontSize = LocalAppDimens.current.billItemTitle
            )
            Text(
                "จำนวน: ${item.quantity}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "%.2f".format(item.pricePerUnit * item.quantity),
            fontWeight = FontWeight.SemiBold,
            // [FIXED] ใช้ขนาดจาก Dimens
            fontSize = LocalAppDimens.current.displayTotal,
            color = MaterialTheme.colorScheme.primary
        )
        // ปุ่มลดจำนวน
        OutlinedIconButton(
            onClick = onDecrease,
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease quantity", modifier = Modifier.size(18.dp))
        }
        // ปุ่มเพิ่มจำนวน
        IconButton(
            onClick = onIncrease,
            modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase quantity", tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun QuickItemsGrid(
    modifier: Modifier = Modifier,
    products: List<Product>,
    onProductClick: (Product) -> Unit,
//    onProductLongClick: (Product) -> Unit
) {
    if (products.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("ยังไม่มีสินค้าขายเร็ว\nไปที่ 'ตั้งค่า' เพื่อเพิ่มสินค้า", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = {it.id}) { product ->
                QuickItemButton(
                    product = product,
                    onClick = { onProductClick(product) },
//                    onLongClick = { onProductLongClick(product) }
                )
            }
        }
    }
}

// --- [REVISED] QuickItemButton ---
@Composable
fun QuickItemButton(product: Product, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp), // ขอบมนขึ้น
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
        contentPadding = PaddingValues(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${NumberFormat.getNumberInstance().format(product.price)}.-",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// [FIXED] แก้ไขปุ่ม ActionButtons
@Composable
fun ActionButtons(onCashClick: () -> Unit, onQrClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onCashClick,
            modifier = Modifier
                .weight(1f)
                // [FIXED] ใช้ขนาดจาก Dimens
                .height(LocalAppDimens.current.actionButtonHeight),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60)) // สีเขียวใหม่
        ) {
            Text(
                "รับเงินสด",
                // [FIXED] ใช้ขนาดจาก Dimens
                fontSize = LocalAppDimens.current.actionButtonText,
                fontWeight = FontWeight.Bold,
                softWrap = false // ป้องกันการขึ้นบรรทัดใหม่
            )
        }
        Button(
            onClick = onQrClick,
            modifier = Modifier
                .weight(1f)
                // [FIXED] ใช้ขนาดจาก Dimens
                .height(LocalAppDimens.current.actionButtonHeight),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2980B9)) // สีน้ำเงินใหม่
        ) {
            Text(
                "สแกน QR จ่าย",
                fontSize = LocalAppDimens.current.actionButtonText,
                fontWeight = FontWeight.Bold,
                softWrap = false // ป้องกันการขึ้นบรรทัดใหม่
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun SalesScreenPreview() { HamsterPOSTheme { SalesScreen() } }
