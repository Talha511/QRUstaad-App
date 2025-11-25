package com.example.qrustaad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.qrustaad.navigation.AppNavigation
import com.example.qrustaad.ui.theme.QRUstaadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QRUstaadTheme {
                AppNavigation()
            }
        }
    }
}