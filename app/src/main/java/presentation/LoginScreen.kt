package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import com.example.parcial_sebastiangranoblesardila.viewmodel.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, userViewModel: UserViewModel) {
    val AppRed = Color(0xFFD32F2F)
    val AppGrey = Color(0xFF1C1C1C) // Gris oscuro PIT-LANE

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by userViewModel.authState.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()

    // Restauración de la lógica de redirección
    LaunchedEffect(authState) {
        if (authState == AuthState.SUCCESS) {
            navController.navigate("main_route") {
                popUpTo("login_route") { inclusive = true }
            }
            userViewModel.resetAuthState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo PIT-LANE con icono de carro
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = AppRed,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "PIT-LANE",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = AppRed,
                letterSpacing = 2.sp
            )
            Text(
                text = "CARS & SERVICES",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Campo de Email restaurado
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, null, tint = AppRed) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AppRed,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = AppRed
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña restaurado
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = AppRed) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AppRed,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = AppRed
                ),
                singleLine = true
            )

            // Olvido de contraseña
            TextButton(
                onClick = { navController.navigate("recover_password_route") },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("¿Olvidaste tu contraseña?", color = AppRed.copy(alpha = 0.8f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Acción Principal
            if (authState == AuthState.LOADING) {
                CircularProgressIndicator(color = AppRed)
            } else {
                Button(
                    onClick = { userViewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                ) {
                    Text("ENTRAR", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
                }
            }

            // Mensaje de Error dinámico
            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = AppRed, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Registro de cuenta nueva
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Nuevo en PIT-LANE?", color = Color.White)
                TextButton(onClick = { navController.navigate("register_route") }) {
                    Text("Crea una cuenta", color = AppRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
