package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController, userViewModel: UserViewModel) {
    val PitRed = Color(0xFFD32F2F)
    val PitDarkGrey = Color(0xFF1C1C1C)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PANEL DE MODERACIÓN", color = Color.White, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PitRed)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PitDarkGrey)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "ADMINISTRACIÓN PIT-LANE",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Sección MODERACIÓN
            Text(
                "MODERACIÓN",
                color = PitRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            HorizontalDivider(color = PitRed.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    AdminCard(
                        title = "Mecánicos",
                        icon = Icons.Default.Build,
                        color = PitRed,
                        onClick = { navController.navigate("mechanics_management_route") }
                    )
                }
                item {
                    AdminCard(
                        title = "Control Citas",
                        icon = Icons.Default.DateRange,
                        color = PitRed,
                        onClick = { navController.navigate("admin_appointments_route") }
                    )
                }
                item {
                    AdminCard(
                        title = "Historial Clientes",
                        icon = Icons.Default.History,
                        color = PitRed,
                        onClick = { navController.navigate("history_route") }
                    )
                }
                item {
                    AdminCard(
                        title = "Configuración",
                        icon = Icons.Default.Settings,
                        color = PitRed,
                        onClick = { /* Implementar si es necesario */ }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminCard(title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
