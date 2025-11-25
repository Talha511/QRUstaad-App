package com.example.qrustaad.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.*
import com.example.qrustaad.screens.createqr.CreateQRScreen
import com.example.qrustaad.screens.home.HomeScreen
import com.example.qrustaad.screens.scanqr.ScanQRScreen
import com.example.qrustaad.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    var scannedData by remember { mutableStateOf<String?>(null) }

    AnimatedContent(targetState = currentScreen, label = "nav") { screen ->
        when (screen) {
            Screen.SPLASH -> SplashScreen(onGetStarted = {
                currentScreen = Screen.HOME
            })

            Screen.HOME -> HomeScreen(
                onNavigate = { currentScreen = it },
                scannedData = scannedData
            )

            Screen.CREATE -> CreateQRScreen(
                onNavigate = { currentScreen = it }
            )

            Screen.SCAN -> ScanQRScreen(
                onNavigate = { currentScreen = it },
                onQRScanned = { value ->
                    scannedData = value
                    currentScreen = Screen.HOME
                }
            )
        }
    }
}
