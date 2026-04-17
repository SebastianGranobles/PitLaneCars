package com.example.parcial_sebastiangranoblesardila.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.Appointment
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, userViewModel: UserViewModel) {
    val PitRed = Color(0xFFD32F2F)
    val PitGrey = Color(0xFF1C1C1C)
    val PitMediumGrey = Color(0xFF2C2C2C)
    val PlateYellow = Color(0xFFFFD54F)

    val user by userViewModel.user.collectAsState()
    val appointments by userViewModel.appointments.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Box(modifier = Modifier.fillMaxSize().background(PitMediumGrey)) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("PIT-LANE MENU", modifier = Modifier.padding(16.dp), color = PitRed, fontWeight = FontWeight.Black)
                        HorizontalDivider(color = PitRed.copy(alpha = 0.3f))

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.History, contentDescription = null, tint = PitRed) },
                            label = { Text("Historial de Órdenes", color = Color.White) },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); navController.navigate("history_route") } },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )

                        if (user?.role == "ADMIN") {
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = PitRed) },
                                label = { Text("MODERACIÓN", color = Color.White) },
                                selected = false,
                                onClick = { scope.launch { drawerState.close(); navController.navigate("admin_dashboard_route") } },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        NavigationDrawerItem(
                            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = PitRed) },
                            label = { Text("Cerrar Sesión", color = PitRed) },
                            selected = false,
                            onClick = { 
                                scope.launch { 
                                    drawerState.close()
                                    userViewModel.logout()
                                    navController.navigate("login_route") {
                                        popUpTo(0)
                                    }
                                } 
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("PIT-LANE", fontWeight = FontWeight.Black, color = Color.White, fontSize = 24.sp) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PitRed)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("appointment_route") },
                    containerColor = PitRed,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva Orden")
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().background(PitGrey).padding(padding)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        modifier = Modifier.size(130.dp),
                        shape = CircleShape,
                        color = PitMediumGrey,
                        border = BorderStroke(4.dp, PitRed),
                        shadowElevation = 8.dp
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(25.dp).fillMaxSize(), tint = PitRed)
                    }

                    Text(text = user?.fullName?.uppercase() ?: "USUARIO", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(text = "CARGO: ${user?.role ?: "ASESOR"}", fontSize = 14.sp, color = PitRed, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = "ÓRDENES ACTIVAS", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = PitRed, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))

                    if (appointments.isNotEmpty()) {
                        appointments.forEach { cita ->
                            AppointmentCard(cita, userViewModel, currencyFormat, PitRed, PlateYellow, user?.role ?: "ASESOR")
                        }
                    } else {
                        Text(text = "No hay órdenes activas", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(30.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { navController.navigate("profile_route") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PitMediumGrey),
                        border = BorderStroke(1.dp, PitRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Settings, null, tint = PitRed)
                        Spacer(Modifier.width(8.dp))
                        Text("AJUSTES DE PERFIL", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    cita: Appointment,
    userViewModel: UserViewModel,
    currencyFormat: NumberFormat,
    PitRed: Color,
    plateYellow: Color,
    userRole: String
) {
    val context = LocalContext.current
    var showFinishConfirm by remember { mutableStateOf(false) }

    val clientParts = (cita.clientName ?: "").split("|")
    val nameOnly = clientParts.getOrNull(0)?.trim() ?: "SIN NOMBRE"
    val usageType = if (clientParts.size > 1) clientParts[1].trim() else "N/A"

    if (showFinishConfirm) {
        AlertDialog(
            onDismissRequest = { showFinishConfirm = false },
            containerColor = Color(0xFF2C2C2C),
            title = { Text("Finalizar Servicio", color = Color.White) },
            text = { Text("¿Deseas marcar la orden ${cita.plate} como terminada?", color = Color.LightGray) },
            confirmButton = {
                TextButton(onClick = {
                    showFinishConfirm = false
                    userViewModel.updateAppointmentStatus(cita.id, "Listo")
                }) {
                    Text("SÍ, FINALIZAR", color = PitRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showFinishConfirm = false }) { Text("CANCELAR", color = Color.Gray) } }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252525)),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, PitRed.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "CLIENTE: ${nameOnly.uppercase()}", fontSize = 11.sp, color = PitRed, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Surface(color = PitRed, shape = RoundedCornerShape(4.dp)) {
                            Text(text = usageType.uppercase(), color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(text = "${cita.brand} ${cita.model}".uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                }

                if (userRole == "ADMIN") {
                    IconButton(onClick = {
                        userViewModel.deleteAppointment(cita.id)
                    }) {
                        Icon(Icons.Default.Delete, null, tint = PitRed.copy(alpha = 0.8f))
                    }
                }
            }

            Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconInfo(Icons.Default.SettingsInputComponent, "DATOS: ${cita.displacement}")
                IconInfo(Icons.Default.Speed, "${cita.mileage} KM")
                IconInfo(Icons.Default.Event, "AÑO: ${cita.year}")
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = plateYellow, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Color.Black)) {
                        Text(text = (cita.plate ?: "").uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${cita.phone1}"))
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Call, null, tint = Color(0xFF4CAF50))
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "MECÁNICO", fontSize = 9.sp, color = PitRed)
                    Text(text = (cita.mechanic ?: "Sin asignar").uppercase(), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }

            if ((userRole == "ADMIN" || userRole == "MECANICO") && cita.status != "Listo") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showFinishConfirm = true },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PitRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("FINALIZAR ORDEN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.DarkGray)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "TOTAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PitRed)
                    Text(text = currencyFormat.format(cita.totalCost), fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
                Text(text = cita.entryDate ?: "", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun IconInfo(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(16.dp), tint = Color.Gray)
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}
