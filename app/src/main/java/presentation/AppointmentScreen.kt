package com.example.parcial_sebastiangranoblesardila.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppointmentScreen(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val AppRed = Color(0xFFD32F2F)
    val AppGrey = Color(0xFF1C1C1C)
    val AppMediumGrey = Color(0xFF2C2C2C)
    val scrollState = rememberScrollState()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    // --- DATA DE SERVICIOS ---
    val motoPrices = mapOf(
        "INSPECCIÓN" to mapOf("Arranque" to 25000, "Ruidos anormales" to 20000, "Fugas" to 20000, "Luces" to 25000, "Frenos" to 25000, "Cadena / transmisión" to 15000, "Llantas" to 15000, "Suspensión" to 20000, "Tablero / testigos" to 25000),
        "MANTENIMIENTO" to mapOf("Cambio de aceite" to 5000, "Cambio de filtros" to 15000, "Ajuste y lubricación de cadena" to 5000, "Revisión general" to 40000, "Mantenimiento preventivo" to 150000),
        "MECÁNICA" to mapOf("Reparación de motor" to 150000, "Ajuste de válvulas" to 70000, "Cambio de kit de arrastre" to 25000, "Cambio de embrague" to 50000, "Reparación de fugas de aceite" to 60000),
        "FRENOS" to mapOf("Cambio de pastillas" to 15000, "Purgado de frenos" to 5000, "Cambio de líquido de frenos" to 10000, "Reparación de bomba / cáliper" to 20000),
        "ELÉCTRICO" to mapOf("Diagnóstico eléctrico" to 50000, "Cambio de batería" to 15000, "Reparación de cableado" to 50000, "Revisión sistema de carga" to 20000, "Instalación de accesorios" to 40000),
        "COMBUSTIBLE" to mapOf("Limpieza de carburador" to 40000, "Limpieza de inyectores" to 40000, "Diagnóstico de inyección electrónica" to 50000, "Cambio de filtro de combustible" to 25000),
        "SUSPENSIÓN" to mapOf("Cambio de retenes" to 35000, "Mantenimiento de horquilla" to 35000, "Cambio de llantas" to 15000, "Parcheo / balanceo" to 15000, "Cambio de rodamientos" to 15000),
        "OTROS" to mapOf("Diagnóstico por escáner" to 60000, "Revisión pre-viaje" to 50000, "Revisión para tecnomecánica" to 40000, "Lavado de motor" to 60000, "Personalización básica" to 50000)
    )

    val carroPrices = mapOf(
        "INSPECCIÓN" to mapOf("Arranque" to 40000, "Ruidos anormales" to 35000, "Fugas" to 35000, "Luces" to 40000, "Frenos" to 50000, "Transmisión" to 40000, "Llantas" to 30000, "Suspensión" to 45000, "Tablero / testigos" to 50000),
        "MANTENIMIENTO" to mapOf("Cambio de aceite" to 40000, "Cambio de filtros" to 5000, "Revisión general" to 80000, "Mantenimiento preventivo" to 180000),
        "MECÁNICA" to mapOf("Reparación de motor" to 300000, "Ajuste de válvulas" to 150000, "Cambio de embrague" to 250000, "Reparación de fugas de aceite" to 120000),
        "FRENOS" to mapOf("Cambio de pastillas" to 80000, "Purgado de frenos" to 20000, "Cambio de líquido de frenos" to 30000, "Reparación de cáliper" to 80000),
        "ELÉCTRICO" to mapOf("Diagnóstico eléctrico" to 90000, "Cambio de batería" to 30000, "Reparación de cableado" to 90000, "Revisión sistema de carga" to 50000, "Instalación de accesorios" to 80000),
        "COMBUSTIBLE" to mapOf("Limpieza de inyectores" to 90000, "Diagnóstico de inyección electrónica" to 100000, "Cambio de filtro de combustible" to 40000),
        "SUSPENSIÓN" to mapOf("Cambio de amortiguadores" to 120000, "Cambio de llantas" to 30000, "Balanceo" to 30000, "Cambio de rodamientos" to 60000),
        "OTROS" to mapOf("Diagnóstico por escáner" to 120000, "Revisión pre-viaje" to 80000, "Revisión tecnomecánica (pre-chequeo)" to 70000, "Lavado de motor" to 80000)
    )

    // --- ESTADOS ---
    var vehicleType by remember { mutableStateOf("Moto") }
    var plate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var displacement by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var phone1 by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val selectedServices = remember { mutableStateListOf<String>() }
    var laborCost by remember { mutableStateOf("") }
    var selectedMechanic by remember { mutableStateOf("") }

    val mechanicsList = listOf("Sebastian Granobles", "Andrés Mendoza", "Juan Perez", "Carlos Rodriguez")

    // --- LÓGICA ---
    val currentPrices = if (vehicleType == "Moto") motoPrices else carroPrices
    val flatPrices = currentPrices.values.flatMap { it.entries }.associate { it.key to it.value }
    val servicesTotal = selectedServices.sumOf { flatPrices[it] ?: 0 }.toDouble()
    val totalToPay = (laborCost.toDoubleOrNull() ?: 0.0) + servicesTotal

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PIT-LANE ORDEN", color = Color.White, fontWeight = FontWeight.Black) },
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
            modifier = Modifier.fillMaxSize().padding(padding).background(AppGrey).verticalScroll(scrollState).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionTitle("0. TIPO DE VEHÍCULO", AppRed)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VehicleTypeCard("MOTO", Icons.Default.TwoWheeler, vehicleType == "Moto", { vehicleType = "Moto"; selectedServices.clear() }, Modifier.weight(1f), AppRed)
                VehicleTypeCard("CARRO", Icons.Default.DirectionsCar, vehicleType == "Carro", { vehicleType = "Carro"; selectedServices.clear() }, Modifier.weight(1f), AppRed)
            }

            SectionTitle("1. DATOS DEL VEHÍCULO", AppRed)
            OutlinedTextField(value = plate, onValueChange = { if (it.length <= 6) plate = it.uppercase() }, label = { Text("Placa *") }, modifier = Modifier.fillMaxWidth(), colors = getTextFieldColors(AppRed))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(brand, { brand = it }, label = { Text("Marca") }, modifier = Modifier.weight(1f), colors = getTextFieldColors(AppRed))
                OutlinedTextField(model, { model = it }, label = { Text("Referencia") }, modifier = Modifier.weight(1f), colors = getTextFieldColors(AppRed))
            }

            SectionTitle("2. DATOS DEL CLIENTE", AppRed)
            OutlinedTextField(clientName, { clientName = it }, label = { Text("Nombre Cliente *") }, modifier = Modifier.fillMaxWidth(), colors = getTextFieldColors(AppRed))
            OutlinedTextField(email, { email = it }, label = { Text("Correo Electrónico (Para PDF) *") }, modifier = Modifier.fillMaxWidth(), colors = getTextFieldColors(AppRed), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            OutlinedTextField(phone1, { if (it.length <= 10) phone1 = it }, label = { Text("Celular *") }, modifier = Modifier.fillMaxWidth(), colors = getTextFieldColors(AppRed), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))

            SectionTitle("3. SERVICIOS (${vehicleType.uppercase()})", AppRed)

            // --- ORGANIZACIÓN 2 EN 2 ---
            val categories = currentPrices.keys.toList()
            for (i in categories.indices step 2) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Categoría 1
                    Column(modifier = Modifier.weight(1f)) {
                        ServiceCategory(categories[i], currentPrices[categories[i]]!!, selectedServices, AppRed, currencyFormat)
                    }
                    // Categoría 2 (Si existe)
                    if (i + 1 < categories.size) {
                        Column(modifier = Modifier.weight(1f)) {
                            ServiceCategory(categories[i+1], currentPrices[categories[i+1]]!!, selectedServices, AppRed, currencyFormat)
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            SectionTitle("4. COSTOS Y PERSONAL", AppRed)
            OutlinedTextField(value = laborCost, onValueChange = { laborCost = it }, label = { Text("Mano de Obra adicional ($)") }, modifier = Modifier.fillMaxWidth(), colors = getTextFieldColors(AppRed), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = AppMediumGrey), border = androidx.compose.foundation.BorderStroke(1.dp, AppRed)) {
                Column(Modifier.padding(16.dp)) {
                    Text("COSTO SERVICIOS: ${currencyFormat.format(servicesTotal)}", color = Color.Gray, fontSize = 14.sp)
                    Text("TOTAL A PAGAR: ${currencyFormat.format(totalToPay)}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
            }

            AppointmentDropdown("Mecánico Asignado *", mechanicsList, { selectedMechanic = it }, AppRed, AppMediumGrey)

            Button(
                onClick = {
                    val now = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                    val appointment = Appointment(
                        clientName = "$clientName | $vehicleType",
                        phone1 = phone1, plate = plate, brand = brand, model = model,
                        displacement = displacement, year = year, mileage = mileage,
                        mechanic = selectedMechanic, problemDescription = "Vehículo: $vehicleType",
                        selectedServices = selectedServices.toList(),
                        laborCost = laborCost.toDoubleOrNull() ?: 0.0,
                        partsCost = servicesTotal,
                        totalCost = totalToPay,
                        entryDate = now, status = "En Taller"
                    )

                    // Lógica: Guardar en DB y Enviar PDF
                    userViewModel.addAppointment(appointment)
                    generateAndShareOrderPDF(context, appointment, email)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = plate.length >= 5 && clientName.isNotEmpty() && email.contains("@") && selectedMechanic.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                shape = RoundedCornerShape(12.dp)
            ) { Text("GENERAR ORDEN Y ENVIAR PDF", fontWeight = FontWeight.Bold, color = Color.White) }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- FUNCIONES DE APOYO ---

fun generateAndShareOrderPDF(context: Context, appointment: Appointment, targetEmail: String) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    // Dibujado del PDF (Simplificado para el ejemplo)
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 24f
    paint.color = android.graphics.Color.RED
    canvas.drawText("PIT-LANE - ORDEN DE SERVICIO", 50f, 50f, paint)

    paint.color = android.graphics.Color.BLACK
    paint.textSize = 14f
    canvas.drawText("Cliente: ${appointment.clientName}", 50f, 100f, paint)
    canvas.drawText("Placa: ${appointment.plate}", 50f, 120f, paint)
    canvas.drawText("Vehículo: ${appointment.brand} ${appointment.model}", 50f, 140f, paint)
    canvas.drawText("Fecha: ${appointment.entryDate}", 50f, 160f, paint)
    canvas.drawText("Total: $${appointment.totalCost}", 50f, 200f, paint)

    pdfDocument.finishPage(page)

    val file = File(context.cacheDir, "Orden_PitLane_${appointment.plate}.pdf")
    pdfDocument.writeTo(FileOutputStream(file))
    pdfDocument.close()

    // Compartir por Email
    val uri = FileProvider.getUriForFile(context, "com.example.parcial_sebastiangranoblesardila.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(targetEmail))
        putExtra(Intent.EXTRA_SUBJECT, "Orden de Servicio PIT-LANE - ${appointment.plate}")
        putExtra(Intent.EXTRA_TEXT, "Adjuntamos su orden de servicio generada en PIT-LANE.")
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Enviar correo..."))
}

@Composable
fun getTextFieldColors(accent: Color) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
    focusedBorderColor = accent, unfocusedBorderColor = Color.Gray,
    focusedLabelColor = accent, unfocusedLabelColor = Color.Gray,
    cursorColor = accent
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceCategory(title: String, services: Map<String, Int>, selectedServices: MutableList<String>, accent: Color, format: NumberFormat) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accent)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            services.forEach { (service, price) ->
                val isSelected = selectedServices.contains(service)
                FilterChip(
                    selected = isSelected,
                    onClick = { if (isSelected) selectedServices.remove(service) else selectedServices.add(service) },
                    label = {
                        Column {
                            Text(service, fontSize = 9.sp)
                            Text(format.format(price), fontSize = 7.sp)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = accent, selectedLabelColor = Color.White)
                )
            }
        }
    }
}

@Composable
fun VehicleTypeCard(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier, AppRed: Color) {
    Card(
        onClick = onClick,
        modifier = modifier.height(70.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) AppRed else Color(0xFF333333)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = if (isSelected) Color.White else Color.Gray)
            Text(label, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun SectionTitle(title: String, color: Color) { Text(title, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDropdown(label: String, options: List<String>, onSelect: (String) -> Unit, AppRed: Color, AppBg: Color) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedText, onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = getTextFieldColors(AppRed)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(AppBg)) {
            options.forEach { option -> DropdownMenuItem(text = { Text(option, color = Color.White) }, onClick = { selectedText = option; onSelect(option); expanded = false }) }
        }
    }
}