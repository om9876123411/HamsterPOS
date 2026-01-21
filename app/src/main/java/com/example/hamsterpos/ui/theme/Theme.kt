package com.example.hamsterpos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
/*
// กำหนดชุดสีสำหรับ Light Theme
private val LightColorScheme = lightColorScheme(
    primary = HamsterBlue,
    secondary = HamsterOrange,
    tertiary = HamsterBlueLight,
    background = HamsterBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = HamsterText,
    onSurface = HamsterText,
)
*/
// กำหนดชุดสีสำหรับ Dark Theme (เผื่ออนาคต)
private val DarkColorScheme = darkColorScheme(
    primary = HamsterBlueDark,
    secondary = HamsterOrange,
    background = HamsterSurfaceDark,
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)
/*
@Composable
fun HamsterPOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // เราจะใช้ Typography ที่มีอยู่แล้วไปก่อน
        content = content
    )
}
*/

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryYellow,
    background = LightBackground,
    surface = SurfaceWhite,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = GreyText,
    error = Color(0xFFE74C3C)
)

// หมายเหตุ: เราจะยังไม่ทำ Dark Theme ในเวอร์ชันนี้เพื่อความรวดเร็ว
// private val DarkColorScheme = darkColorScheme(...)

@Composable
fun HamsterPOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    // [NEW] ตรรกะการเลือกชุดขนาดอัตโนมัติ
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    // ถ้าความกว้างหน้าจอน้อยกว่า 380dp ให้ใช้ชุดขนาดเล็ก
    val dimens = if (screenWidthDp < 380) CompactDimens else NormalDimens

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    // [NEW] ส่งชุดขนาดที่เลือก (dimens) ไปให้ Composable ลูกๆ ผ่าน CompositionLocalProvider
    CompositionLocalProvider(LocalAppDimens provides dimens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
