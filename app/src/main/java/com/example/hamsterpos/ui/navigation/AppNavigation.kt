// Path: app/src/main/java/com/example/hamsterpos/ui/navigation/AppNavigation.kt
package com.example.hamsterpos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hamsterpos.ui.screen.main.MainScreen
import com.example.hamsterpos.ui.screen.setup.SetupScreen

sealed class AppScreen(val route: String) {
    object Setup : AppScreen("setup_screen")
    object Main : AppScreen("main_screen")
}

@Composable
fun AppNavigation(
    startDestination: String // [EDITED] รับค่า start destination มาจากข้างนอก
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination // [EDITED] ใช้ค่าที่ได้รับมา
    ) {
        composable(route = AppScreen.Setup.route) {
            SetupScreen(
                onNavigateToMain = {
                    navController.navigate(AppScreen.Main.route) {
                        popUpTo(AppScreen.Setup.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = AppScreen.Main.route) {
            MainScreen()
        }
    }
}
