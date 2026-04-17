package com.example.parcial_sebastiangranoblesardila.presentation

import android.content.Intent
import android.icu.text.NumberFormat
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import com.example.parcial_sebastiangranoblesardila.viewmodel.Appointment
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.user.collectAsState()
    val rawAppointments by userViewModel.appointments.collectAsState()
    val rawFinishedAppointments by userViewModel.finishedAppointments.collectAsState()

    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    val appointments = remember(rawAppointments) {
        rawAppointments.sortedByDescending {
            try { sdf.parse(it.entryDate)?.time ?: 0L } catch (e: Exception) { 0L }
        }
    }

    val finishedAppointments = remember(rawFinishedAppointments) {
        rawFinishedAppointments.sortedByDescending {
            try { sdf.parse(it.entryDate)?.time ?: 0L } catch (e: Exception) { 0L }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val AppRed = Color(0xFFD32F2F)
    val AppGrey = Color(0xFF2C2C2C)
    val AppLightGrey = Color(0xFF3E3E3E)
    val plateYellow = Color(0xFFFFD54F)
    val myBlue = Color(0xFF1976D2)

    val localeCo = Locale.Builder().setLanguage("es").setRegion("CO").build()
    val currencyFormat = NumberFormat.getCurrencyInstance(localeCo).apply {
        maximumFractionDigits = 0
    }

    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("login_route") {
                popUpTo("main_route") { inclusive = true }
            }
        } else {
            userViewModel.startAppointmentsRealtimeListener()
            FirebaseMessaging.getInstance().subscribeToTopic("taller")
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = AppGrey,
            dragHandle = { BottomSheetDefaults.DragHandle(color = AppRed) }
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 32.dp)) {
                Text(
                    text = "HISTORIAL DE CITAS TERMINADAS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppRed,
                    modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
                )
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    if (finishedAppointments.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No hay registros terminados.", color = Color.Gray)
                        }
                    } else {
                        finishedAppointments.forEach { cita ->
                            AppointmentCard(cita, userViewModel, currencyFormat, AppRed, plateYellow, Color.Gray, myBlue, isHistory = true, userRole = user?.role ?: "ASESOR")
                        }
                    }
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerShape = RoundedCornerShape(0.dp), drawerContainerColor = AppGrey) {
                Box(modifier = Modifier.fillMaxWidth().background(AppRed).padding(24.dp)) {
                    Column {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Text("PIT-LANE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (user?.role == "ADMIN" || user?.role == "ASESOR") {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Build, contentDescription = null, tint = Color.White) },
                        label = { Text("Agendar Cita", color = Color.White) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close(); delay(100); navController.navigate("appointment_route") } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                }

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null, tint = myBlue) },
                    label = { Text("CITAS TERMINADAS", color = myBlue, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close(); showBottomSheet = true } },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = AppRed) },
                    label = { Text("Cerrar Sesión", color = AppRed) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close(); userViewModel.logout() } },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppRed)
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().background(AppGrey).padding(padding)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(modifier = Modifier.size(130.dp), shape = CircleShape, color = AppLightGrey, border = BorderStroke(4.dp, AppRed), shadowElevation = 8.dp) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(25.dp).fillMaxSize(), tint = Color.Gray)
                    }
                    Text(text = user?.fullName?.uppercase() ?: "ADMINISTRADOR", fontSize = 26.sp, fontWeight = FontWeight.Black, color = AppRed)
                    Text(text = "CARGO: ${user?.role ?: "ASESOR"}", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = "ÓRDENES ACTIVAS", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AppRed, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))

                    if (appointments.isNotEmpty()) {
                        appointments.forEach { cita ->
                            AppointmentCard(cita, userViewModel, currencyFormat, AppRed, plateYellow, Color.Gray, myBlue, false, user?.role ?: "ASESOR")
                        }
                    } else {
                        Text(text = "No hay órdenes activas", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(30.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { navController.navigate("profile_route") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Settings, null, tint = Color.White)
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
    AppRed: Color,
    plateYellow: Color,
    myGrey: Color,
    myBlue: Color,
    isHistory: Boolean,
    userRole: String
) {
    val context = LocalContext.current
    var showFinishConfirm by remember { mutableStateOf(false) }

    val clientParts = cita.clientName.split("|")
    val nameOnly = clientParts[0].trim()
    val usageType = if (clientParts.size > 1) clientParts[1].trim() else "N/A"

    if (showFinishConfirm) {
        AlertDialog(
            onDismissRequest = { showFinishConfirm = false },
            title = { Text("Finalizar Servicio") },
            text = { Text("¿Deseas marcar la orden ${cita.plate} como terminada?") },
            confirmButton = {
                TextButton(onClick = {
                    showFinishConfirm = false
                    userViewModel.finishAppointment(cita.id)
                }) {
                    Text("SÍ, FINALIZAR", color = myBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showFinishConfirm = false }) { Text("CANCELAR") } }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E3E3E)),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isHistory) Color.Gray else AppRed.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "CLIENTE: ${nameOnly.uppercase()}", fontSize = 11.sp, color = AppRed, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Surface(color = Color.DarkGray, shape = RoundedCornerShape(4.dp)) {
                            Text(text = usageType.uppercase(), color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(text = "${cita.brand} ${cita.model}".uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
                if (userRole == "ADMIN") {
                    IconButton(onClick = {
                        if (isHistory) userViewModel.removeFinishedAppointment(cita.id)
                        else userViewModel.removeAppointment(cita.id)
                    }) {
                        Icon(Icons.Default.Delete, null, tint = AppRed.copy(alpha = 0.8f))
                    }
                }
            }

            Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconInfo(Icons.Default.SettingsInputComponent, "CIL: ${cita.displacement}")
                IconInfo(Icons.Default.Speed, "${cita.mileage} KM")
                IconInfo(Icons.Default.Event, "AÑO: ${cita.year}")
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(0.6f)) {
                    Surface(color = plateYellow, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Color.Black)) {
                        Text(text = cita.plate.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${cita.phone1}"))
                            context.startActivity(intent)
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Call, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = cita.phone1, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(0.4f)) {
                    Text(text = "MECÁNICO", fontSize = 9.sp, color = Color.LightGray)
                    Text(text = cita.mechanic.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (cita.selectedServices.isNotEmpty()) {
                Text(text = "SERVICIOS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AppRed)
                Column(modifier = Modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                    cita.selectedServices.forEach { servicio ->
                        Text(text = "• $servicio", fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            if (!isHistory && (userRole == "ADMIN" || userRole == "MECANICO")) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showFinishConfirm = true },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = myBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("FINALIZAR ORDEN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.Gray)

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "MANO DE OBRA", fontSize = 11.sp, color = Color.LightGray)
                    Text(text = currencyFormat.format(cita.laborCost), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "REPUESTOS", fontSize = 11.sp, color = Color.LightGray)
                    Text(text = currencyFormat.format(cita.partsCost), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "TOTAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppRed)
                        Text(
                            text = currencyFormat.format(cita.totalCost),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isHistory) Color(0xFF4CAF50) else AppRed
                        )
                    }
                    Text(text = cita.entryDate, color = Color.LightGray, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun IconInfo(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(16.dp), tint = Color.LightGray)
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}
