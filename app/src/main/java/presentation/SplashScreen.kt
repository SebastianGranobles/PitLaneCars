package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.R
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, userViewModel: UserViewModel) {
    val PitDarkGrey = Color(0xFF1C1C1C)
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(2000L)
        
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
            .background(PitDarkGrey)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_pit_lane),
                contentDescription = "Logo PIT-LANE",
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale.value)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "CARS & SERVICES",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp,
                modifier = Modifier.scale(scale.value)
            )
        }
    }
}
