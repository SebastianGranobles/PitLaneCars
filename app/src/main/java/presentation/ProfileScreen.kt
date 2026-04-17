package com.example.parcial_sebastiangranoblesardila.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    val AppRed = Color(0xFFD32F2F)
    val AppGrey = Color(0xFF2C2C2C)
    val AppLightGrey = Color(0xFF3E3E3E)

    val user by userViewModel.user.collectAsState()
    val isLockedState by userViewModel.isProfileEditingLocked.collectAsState()
    val nationalities = userViewModel.nationalities

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedNationality by remember { mutableStateOf("Colombiana") }
    var remainingTime by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var expandedNationality by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = AppRed,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = AppRed,
        unfocusedLabelColor = Color.Gray
    )

    LaunchedEffect(user) {
        user?.let {
            name = it.fullName
            phone = it.phone
            age = it.age
            city = it.city
            selectedNationality = if (it.nationality.isNotBlank()) it.nationality else "Colombiana"
        }
    }

    LaunchedEffect(isLockedState, user) {
        if (isLockedState && user != null) {
            while (true) {
                val lastUpdate = user?.lastProfileUpdateTime ?: 0L
                val remainingMillis = (lastUpdate + 3 * 60 * 60 * 1000) - System.currentTimeMillis()
                if (remainingMillis <= 0) break

                val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60
                remainingTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                delay(1000)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar cuenta?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción es permanente y borrará todos tus datos.") },
            confirmButton = {
                TextButton(onClick = {
                    userViewModel.deleteAccount()
                    navController.navigate("login_route") { popUpTo(0) }
                }) {
                    Text("BORRAR", color = AppRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MI PERFIL TÉCNICO", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppRed),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        userViewModel.logout()
                        navController.navigate("login_route") { popUpTo(0) }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar Sesión", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(AppGrey)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = AppLightGrey,
                border = BorderStroke(4.dp, AppRed),
                shadowElevation = 4.dp
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.padding(25.dp).fillMaxSize(), tint = Color.Gray)
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                enabled = !isLockedState,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                leadingIcon = { Icon(Icons.Default.Badge, null, tint = AppRed) },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Text(text = user?.email ?: "", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                enabled = !isLockedState,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = AppRed) },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Edad") },
                    enabled = !isLockedState,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Ciudad") },
                    enabled = !isLockedState,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
            }

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedNationality && !isLockedState,
                onExpandedChange = { if (!isLockedState) expandedNationality = !expandedNationality }
            ) {
                OutlinedTextField(
                    value = selectedNationality,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nacionalidad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedNationality) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    enabled = !isLockedState,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expandedNationality,
                    onDismissRequest = { expandedNationality = false },
                    modifier = Modifier.background(AppLightGrey)
                ) {
                    nationalities.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = Color.White) },
                            onClick = {
                                selectedNationality = option
                                expandedNationality = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    userViewModel.updateUserInfo(phone, age, city, selectedNationality, name)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLockedState && phone.isNotEmpty() && name.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppRed,
                    disabledContainerColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLockedState) {
                    Icon(Icons.Default.Lock, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("BLOQUEADO POR 3H ($remainingTime)", fontSize = 13.sp)
                } else {
                    Icon(Icons.Default.CloudUpload, null)
                    Spacer(Modifier.width(8.dp))
                    Text("SINCRONIZAR", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { showDeleteDialog = true }) {
                Text("ELIMINAR CUENTA", color = AppRed, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
