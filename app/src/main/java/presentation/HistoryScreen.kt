package com.example.parcial_sebastiangranoblesardila.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.Appointment
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.filter
import kotlin.text.isNotEmpty
import kotlin.text.uppercase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val PitRed = Color(0xFFD32F2F)
    val PitDarkGrey = Color(0xFF1C1C1C)
    val PitMediumGrey = Color(0xFF2C2C2C)
    val PlateYellow = Color(0xFFFFD54F)

    val finishedAppointments by userViewModel.finishedAppointments.collectAsState()
    val user by userViewModel.user.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    // --- FILTRO Y ORDENAMIENTO CRONOLÓGICO ---
    val filteredHistory = finishedAppointments
        .filter { cita ->
            cita.plate.contains(searchQuery, ignoreCase = true) ||
                    cita.brand.contains(searchQuery, ignoreCase = true) ||
                    cita.clientName.contains(searchQuery, ignoreCase = true) ||
                    cita.model.contains(searchQuery, ignoreCase = true)
        }
        .sortedByDescending { cita ->
            try {
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(cita.entryDate)
            } catch (e: Exception) {
                Date(0)
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HISTORIAL PIT-LANE", color = Color.White, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    // BOTÓN EXPORTAR PDF
                    IconButton(onClick = {
                        if (filteredHistory.isNotEmpty()) {
                            generatePDF(context, filteredHistory)
                        } else {
                            Toast.makeText(context, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.PictureAsPdf, "PDF", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PitRed)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PitDarkGrey)
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- BUSCADOR POR COMANDO (PLACA O MARCA) ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar placa, marca o cliente...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = PitRed) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = PitRed,
                    unfocusedBorderColor = Color.Gray,
                    focusedContainerColor = PitMediumGrey,
                    unfocusedContainerColor = PitMediumGrey
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredHistory.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (searchQuery.isEmpty()) "No hay órdenes finalizadas" else "No se encontraron resultados",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredHistory) { cita ->
                        AppointmentCard(
                            cita = cita,
                            userViewModel = userViewModel,
                            currencyFormat = currencyFormat,
                            PitRed = PitRed,
                            plateYellow = PlateYellow,
                            userRole = user?.role ?: "ASESOR"
                        )
                    }
                }
            }
        }
    }
}

// --- FUNCIÓN PARA GENERAR EL DOCUMENTO PDF ---
fun generatePDF(context: Context, data: List<Appointment>) {
    val pdfDocument = PdfDocument()
    val paint = Paint()
    val titlePaint = Paint()

    // Configuración página A4
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas: Canvas = page.canvas

    titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    titlePaint.textSize = 20f
    titlePaint.color = android.graphics.Color.RED

    paint.textSize = 11f
    paint.color = android.graphics.Color.BLACK

    var yPosition = 50f

    // Encabezado del PDF
    canvas.drawText("REPORTE HISTORIAL PIT-LANE", 150f, yPosition, titlePaint)
    yPosition += 40f
    canvas.drawText("Generado el: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}", 40f, yPosition, paint)
    yPosition += 30f
    canvas.drawLine(40f, yPosition, 555f, yPosition, paint)
    yPosition += 30f

    data.forEachIndexed { index, cita ->
        if (yPosition > 750f) { // Salto de página
            pdfDocument.finishPage(page)
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPosition = 50f
        }

        canvas.drawText("${index + 1}. PLACA: ${cita.plate.uppercase()} | FECHA: ${cita.entryDate}", 40f, yPosition, paint)
        yPosition += 18f
        canvas.drawText("   CLIENTE: ${cita.clientName.uppercase()}", 40f, yPosition, paint)
        yPosition += 18f
        canvas.drawText("   VEHÍCULO: ${cita.brand.uppercase()} ${cita.model.uppercase()} | MECÁNICO: ${cita.mechanic}", 40f, yPosition, paint)
        yPosition += 18f
        canvas.drawText("   TOTAL: $${cita.totalCost}", 40f, yPosition, paint)
        yPosition += 25f
        canvas.drawLine(40f, yPosition, 555f, yPosition, paint)
        yPosition += 30f
    }

    pdfDocument.finishPage(page)

    // Guardar archivo en la carpeta de Descargas
    val fileName = "Historial_PitLane_${System.currentTimeMillis()}.pdf"
    val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

    try {
        pdfDocument.writeTo(FileOutputStream(filePath))
        Toast.makeText(context, "PDF guardado en Descargas", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}