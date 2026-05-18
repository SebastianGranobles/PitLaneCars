package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import com.example.parcial_sebastiangranoblesardila.viewmodel.PasswordChangeLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordHistoryScreen(navController: NavController, userViewModel: UserViewModel) {
    val history by userViewModel.passwordChangeHistory.collectAsState()
    val AppRed = Color(0xFFD32F2F)
    val AppGrey = Color(0xFF1C1C1C)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SEGURIDAD", fontWeight = FontWeight.Black, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppRed)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppGrey)
        ) {
            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay cambios registrados.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(history) { logEntry ->
                        HistoryItem(logEntry = logEntry, accentColor = AppRed)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(logEntry: PasswordChangeLog, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, null, tint = accentColor)
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Contraseña actualizada", color = Color.White, fontWeight = FontWeight.Bold)
                Text(logEntry.dateString, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
