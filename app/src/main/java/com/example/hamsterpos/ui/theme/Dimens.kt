// =====================================================================================
// ขั้นตอนที่ 1: สร้างไฟล์ใหม่สำหรับจัดการขนาด (Dimensions)
// =====================================================================================
// สร้างไฟล์ใหม่ทั้งหมดตามนี้ ไฟล์นี้จะเก็บ "ชุดขนาด" ทั้งหมดที่เราจะใช้
// Path: app/src/main/java/com/example/hamsterpos/ui/theme/Dimens.kt
package com.example.hamsterpos.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data class ที่เก็บค่าขนาดต่างๆ ที่เราจะใช้ในแอป
data class AppDimens(
    val welcomeTitle: TextUnit,
    val displayTotal: TextUnit,
    val actionButtonText: TextUnit,
    val actionButtonHeight: Dp,
    val billItemTitle: TextUnit,
    val billItemQuantity: TextUnit
)

// ชุดขนาดสำหรับหน้าจอปกติ (FHD)
val NormalDimens = AppDimens(
    welcomeTitle = 32.sp,
    displayTotal = 52.sp,
    actionButtonText = 18.sp,
    actionButtonHeight = 72.dp,
    billItemTitle = 16.sp,
    billItemQuantity = 12.sp
)

// ชุดขนาดสำหรับหน้าจอเล็ก (HD) หรือมีความละเอียดน้อย
val CompactDimens = AppDimens(
    welcomeTitle = 28.sp,
    displayTotal = 44.sp,
    actionButtonText = 16.sp,
    actionButtonHeight = 64.dp,
    billItemTitle = 15.sp,
    billItemQuantity = 11.sp
)

// สร้าง CompositionLocal เพื่อส่งชุดขนาดที่เลือกไปให้ Composable อื่นๆ ใช้งาน
val LocalAppDimens = compositionLocalOf { NormalDimens }

