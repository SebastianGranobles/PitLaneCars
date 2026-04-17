package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape // <--- ESTE ES EL IMPORT QUE FALTABA
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.AuthState
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarContraseñaScreen(navController: NavController, userViewModel: UserViewModel) {
    // NUEVA PALETA DE COLORES PIT-LANE
    val PitRed = Color(0xFFD32F2F)      // Rojo Racing
    val PitDarkGrey = Color(0xFF1C1C1C) // Gris Fondo
    val PitMediumGrey = Color(0xFF2C2C2C)

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    val authState by userViewModel.authState.collectAsState()
    val firebaseErrorMessage by userViewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        if (authState == AuthState.SUCCESS) {
            coroutineScope.launch { snackbarHostState.showSnackbar("Éxito al cambiar contraseña") }
            userViewModel.resetAuthState()
            navController.popBackStack()
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = PitRed,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = PitRed,
        unfocusedLabelColor = Color.Gray,
        cursorColor = PitRed
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("SEGURIDAD PIT-LANE", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PitRed)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PitDarkGrey) // Fondo Gris PIT-LANE
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("RESTABLECER ACCESO", fontSize = 24.sp, fontWeight = FontWeight.Black, color = PitRed)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Contraseña Actual") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nueva Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            localErrorMessage?.let { Text(it, color = PitRed, modifier = Modifier.padding(top = 8.dp)) }
            firebaseErrorMessage?.let { Text(it, color = PitRed, modifier = Modifier.padding(top = 8.dp)) }

            Spacer(modifier = Modifier.height(32.dp))

            if (authState == AuthState.LOADING) {
                CircularProgressIndicator(color = PitRed)
            } else {
                Button(
                    onClick = {
                        if (newPassword == confirmPassword) {
                            userViewModel.changePassword(currentPassword, newPassword)
                        } else {
                            localErrorMessage = "Las contraseñas no coinciden"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PitRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ACTUALIZAR CONTRASEÑA", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}