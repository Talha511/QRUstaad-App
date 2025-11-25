package com.example.qrustaad.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrustaad.navigation.Screen
import com.example.qrustaad.screens.home.components.DashboardCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (Screen) -> Unit, scannedData: String?) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR USTAAD", fontWeight = FontWeight.SemiBold) }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    label = { Text("History") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            DashboardCard(
                icon = Icons.Default.QrCode2,
                title = "Create QR",
                onClick = { onNavigate(Screen.CREATE) }
            )

            DashboardCard(
                icon = Icons.Default.CameraAlt,
                title = "Scan QR",
                onClick = { onNavigate(Screen.SCAN) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("History", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Card(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (scannedData != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Last Scanned:", fontWeight = FontWeight.SemiBold)
                            Text(scannedData, fontSize = 14.sp)
                        }
                    } else {
                        Text("Your QR history is empty")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onNavigate = {},
            scannedData = "https://example.com"
        )
    }
}
