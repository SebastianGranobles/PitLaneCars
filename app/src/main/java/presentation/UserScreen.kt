package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.user.collectAsState()
    val scrollState = rememberScrollState()

    // Definición de colores locales para mantener consistencia
    val AppRed = Color(0xFFD32F2F)
    val AppLightRed = Color(0xFFFFEBEE)
    val AppGrey = Color(0xFF757575)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("MI PERFIL", fontWeight = FontWeight.ExtraBold, color = Color.White)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppRed),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { userViewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        user?.let { userData ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Brush.verticalGradient(listOf(AppLightRed, Color.White)))
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // AVATAR CON INICIAL
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = AppRed,
                    shadowElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = userData.fullName.take(1).uppercase(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userData.fullName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = userData.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppGrey
                )

                Spacer(modifier = Modifier.height(32.dp))

                // TARJETA DE INFORMACIÓN (ROJO Y BLANCO)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Detalles de la cuenta",
                            fontWeight = FontWeight.Bold,
                            color = AppRed,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        InfoItem(
                            icon = Icons.Default.Phone,
                            label = "Teléfono",
                            value = if (userData.phone.isNotBlank()) userData.phone else "No especificado",
                            AppRed = AppRed, AppGrey = AppGrey
                        )
                        HorizontalDivider(color = AppLightRed, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                        InfoItem(
                            icon = Icons.Default.Cake,
                            label = "Edad",
                            value = if (userData.age.isNotBlank()) userData.age else "No especificada",
                            AppRed = AppRed, AppGrey = AppGrey
                        )
                        HorizontalDivider(color = AppLightRed, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                        InfoItem(
                            icon = Icons.Default.LocationCity,
                            label = "Ciudad",
                            value = if (userData.city.isNotBlank()) userData.city else "No especificada",
                            AppRed = AppRed, AppGrey = AppGrey
                        )
                        HorizontalDivider(color = AppLightRed, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                        InfoItem(
                            icon = Icons.Default.Public,
                            label = "Nacionalidad",
                            value = if (userData.nationality.isNotBlank()) userData.nationality else "No especificada",
                            AppRed = AppRed, AppGrey = AppGrey
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // BOTÓN EDITAR ESTILO PREMIUM
                Button(
                    onClick = { navController.navigate("profile_route") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("EDITAR PERFIL Y AJUSTES", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppRed)
            }
        }
    }
}

@Composable
private fun InfoItem(icon: ImageVector, label: String, value: String, AppRed: Color, AppGrey: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFFEBEE)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = AppRed,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = AppGrey)
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}