package com.example.parcial_sebastiangranoblesardila.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.Appointment
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppointmentScreen(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val AppRed = Color(0xFFD32F2F)
    val AppGrey = Color(0xFF2C2C2C)
    val AppLightGrey = Color(0xFF3E3E3E)
    val scrollState = rememberScrollState()

    val user by userViewModel.user.collectAsState()
    val userRole = user?.role ?: "ASESOR"

    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Toast.makeText(context, "Captura exitosa", Toast.LENGTH_SHORT).show()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val uri = createPhotoUri(context)
                tempUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AppRed,
        unfocusedBorderColor = Color.Gray,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = AppRed,
        unfocusedLabelColor = Color.Gray,
        cursorColor = AppRed,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    )

    val servicePrices = mapOf(
        "Cambio de aceite" to 0.0, "Cambio de filtros" to 0.0, "Mantenimiento Preventivo" to 0.0, "Revisión General" to 0.0,
        "Reparación de motor" to 0.0, "Frenos" to 0.0, "Sistema Eléctrico" to 0.0, "Llantas" to 0.0
    )

    // --- ESTADOS ---
    var vehicleType by remember { mutableStateOf("Moto") }
    var plate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var displacement by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var usageType by remember { mutableStateOf("Uso diario") }
    var clientName by remember { mutableStateOf("") }
    var phone1 by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val selectedServices = remember { mutableStateListOf<String>() }
    var problemDescription by remember { mutableStateOf("") }
    var laborCost by remember { mutableStateOf("") }
    var selectedMechanic by remember { mutableStateOf("") }

    val mechanicsList = listOf("Sebastian Granobles", "Andrés Mendoza", "Juan Perez", "Carlos Rodriguez")

    val isPlateValid = plate.length >= 5
    val isPhoneValid = phone1.length == 10

    val totalToPay = (laborCost.toDoubleOrNull() ?: 0.0) + selectedServices.sumOf { servicePrices[it] ?: 0.0 }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PIT-LANE ORDEN", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppRed),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppGrey)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionTitle("0. TIPO DE VEHÍCULO", AppRed)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                VehicleTypeCard(
                    label = "MOTO",
                    icon = Icons.Default.TwoWheeler,
                    isSelected = vehicleType == "Moto",
                    onClick = { vehicleType = "Moto" },
                    modifier = Modifier.weight(1f),
                    AppRed = AppRed
                )
                VehicleTypeCard(
                    label = "CARRO",
                    icon = Icons.Default.DirectionsCar,
                    isSelected = vehicleType == "Carro",
                    onClick = { vehicleType = "Carro" },
                    modifier = Modifier.weight(1f),
                    AppRed = AppRed
                )
            }

            SectionTitle("1. DATOS DEL VEHÍCULO", AppRed)

            OutlinedTextField(
                value = plate,
                onValueChange = { if (it.length <= 6) plate = it.uppercase() },
                label = { Text("Placa *") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                textStyle = TextStyle(color = Color.White)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Marca") }, modifier = Modifier.weight(1f), colors = textFieldColors, textStyle = TextStyle(color = Color.White))
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Referencia") }, modifier = Modifier.weight(1f), colors = textFieldColors, textStyle = TextStyle(color = Color.White))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = displacement, onValueChange = { displacement = it }, label = { Text("Cilindraje") }, modifier = Modifier.weight(1f), colors = textFieldColors, textStyle = TextStyle(color = Color.White), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = year, onValueChange = { if (it.length <= 4) year = it }, label = { Text("Año") }, modifier = Modifier.weight(1f), colors = textFieldColors, textStyle = TextStyle(color = Color.White), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }

            OutlinedTextField(value = mileage, onValueChange = { mileage = it }, label = { Text("Kilometraje") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors, textStyle = TextStyle(color = Color.White), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            SectionTitle("2. DATOS DEL CLIENTE", AppRed)
            OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Nombre Completo *") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors, textStyle = TextStyle(color = Color.White))

            OutlinedTextField(
                value = phone1,
                onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phone1 = it },
                label = { Text("Celular *") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                textStyle = TextStyle(color = Color.White),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                trailingIcon = {
                    if (isPhoneValid) {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone1"))
                            context.startActivity(intent)
                        }) { Icon(Icons.Default.Call, null, tint = Color(0xFF4CAF50)) }
                    }
                }
            )

            SectionTitle("3. SERVICIOS Y SÍNTOMAS", AppRed)
            ServiceCategory("TALLER", listOf("Cambio de aceite", "Cambio de filtros", "Revisión General", "Mantenimiento Preventivo", "Reparación de motor", "Frenos", "Sistema Eléctrico"), selectedServices, AppRed)

            OutlinedTextField(value = problemDescription, onValueChange = { problemDescription = it }, label = { Text("Descripción de falla") }, modifier = Modifier.fillMaxWidth().height(100.dp), colors = textFieldColors, textStyle = TextStyle(color = Color.White))

            SectionTitle("4. COSTOS Y PERSONAL", AppRed)
            OutlinedTextField(value = laborCost, onValueChange = { laborCost = it }, label = { Text("Mano de Obra ($)") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors, textStyle = TextStyle(color = Color.White), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            AppointmentDropdown(label = "Mecánico Asignado *", options = mechanicsList, onSelect = { selectedMechanic = it }, AppRed = AppRed, AppLightGrey = AppLightGrey)

            SectionTitle("5. EVIDENCIA FOTOGRÁFICA", AppRed)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PhotoStepItem("INGRESO", { requestPermissionLauncher.launch(Manifest.permission.CAMERA) }, Modifier.weight(1f), AppRed)
                PhotoStepItem("SALIDA", { requestPermissionLauncher.launch(Manifest.permission.CAMERA) }, Modifier.weight(1f), AppRed)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val now = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                    val newAppointment = Appointment(
                        clientName = "$clientName | $vehicleType",
                        phone1 = phone1,
                        plate = plate,
                        brand = brand,
                        model = model,
                        displacement = displacement,
                        year = year,
                        mileage = mileage,
                        mechanic = selectedMechanic,
                        problemDescription = "Vehículo: $vehicleType | $problemDescription",
                        selectedServices = selectedServices.toList(),
                        laborCost = laborCost.toDoubleOrNull() ?: 0.0,
                        partsCost = 0.0,
                        totalCost = totalToPay,
                        entryDate = now,
                        status = "En Taller"
                    )
                    userViewModel.addAppointment(newAppointment)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isPlateValid && clientName.isNotEmpty() && isPhoneValid && selectedMechanic.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                shape = RoundedCornerShape(12.dp)
            ) { Text("CREAR ORDEN", fontWeight = FontWeight.Bold, color = Color.White) }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun VehicleTypeCard(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier, AppRed: Color) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) AppRed else Color(0xFF3E3E3E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = if (isSelected) Color.White else Color.Gray)
            Text(label, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun PhotoStepItem(label: String, onTake: () -> Unit, modifier: Modifier, AppRed: Color) {
    Column(modifier = modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFF3E3E3E)).border(1.dp, Color.Gray, RoundedCornerShape(12.dp)).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
        IconButton(onClick = onTake) { Icon(Icons.Default.AddAPhoto, null, tint = AppRed) }
        Text("SUBIR", fontSize = 8.sp, color = Color.Gray)
    }
}

private fun createPhotoUri(context: Context): Uri {
    val directory = File(context.cacheDir, "photos").apply { if (!exists()) mkdirs() }
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val file = File(directory, "IMG_${timeStamp}.jpg")
    val authority = "com.example.parcial_sebastiangranoblesardila.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

@Composable
fun SectionTitle(title: String, color: Color) { Text(title, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color) }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceCategory(title: String, services: List<String>, selectedServices: MutableList<String>, accentColor: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            services.forEach { service ->
                val isSelected = selectedServices.contains(service)
                FilterChip(
                    selected = isSelected,
                    onClick = { if (isSelected) selectedServices.remove(service) else selectedServices.add(service) },
                    label = { Text(service, color = if (isSelected) Color.White else Color.Gray) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = accentColor, containerColor = Color.Transparent)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDropdown(label: String, options: List<String>, onSelect: (String) -> Unit, AppRed: Color, AppLightGrey: Color) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = Color.Gray) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = AppRed,
                unfocusedBorderColor = Color.Gray
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(AppLightGrey)) {
            options.forEach { option -> DropdownMenuItem(text = { Text(option, color = Color.White) }, onClick = { selectedText = option; onSelect(option); expanded = false }) }
        }
    }
}
