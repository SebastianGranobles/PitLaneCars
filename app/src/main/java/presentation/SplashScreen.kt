package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, userViewModel: UserViewModel) {
    // NUEVA PALETA PIT-LANE
    val PitRed = Color(0xFFD32F2F)
    val PitDarkGrey = Color(0xFF1C1C1C)

    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = springSpec(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(2000L) // Tiempo de espera del Splash
        
        // Verificación de sesión para redirigir
        if (userViewModel.isUserLoggedIn()) {
            navController.navigate("main_route") {
                popUpTo("splash_route") { inclusive = true }
            }
        } else {
            navController.navigate("login_route") {
                popUpTo("splash_route") { inclusive = true }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(PitDarkGrey) // Fondo Gris PIT-LANE
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo o Texto Principal
            Text(
                text = "PIT-LANE",
                fontSize = 50.sp,
                fontWeight = FontWeight.Black,
                color = PitRed, // Rojo Racing
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Subtítulo actualizado
            Text(
                text = "CARS & SERVICES",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp
            )
        }
    }
}

// Función auxiliar para el efecto de rebote
fun springSpec(dampingRatio: Float, stiffness: Float): AnimationSpec<Float> {
    return spring(dampingRatio = dampingRatio, stiffness = stiffness)
}