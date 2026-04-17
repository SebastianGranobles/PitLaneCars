package com.example.parcial_sebastiangranoblesardila.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcial_sebastiangranoblesardila.viewmodel.Appointment
import com.example.parcial_sebastiangranoblesardila.viewmodel.UserViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAppointmentsScreen(navController: NavController, userViewModel: UserViewModel) {
    val PitRed = Color(0xFFD32F2F)
    val PitDarkGrey = Color(0xFF1C1C1C)
    
    val appointments by userViewModel.appointments.collectAsState()
    val finishedAppointments by userViewModel.finishedAppointments.collectAsState()
    val allAppointments = appointments + finishedAppointments
    
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CONTROL DE CITAS", color = Color.White, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { exportAppointmentsToPdf(context, allAppointments) }) {
                        Icon(Icons.Default.PictureAsPdf, "Exportar PDF", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PitRed)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PitDarkGrey)
                .padding(16.dp)
        ) {
            Text(
                "LISTADO PROFESIONAL DE ORDENES",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (allAppointments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay citas registradas en el sistema", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(allAppointments.sortedByDescending { it.entryDate }) { appointment ->
                        AdminAppointmentItem(appointment)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAppointmentItem(appointment: Appointment) {
    val statusColor = if (appointment.status == "Listo") Color(0xFF4CAF50) else Color(0xFFFFEB3B)
    val statusIcon = if (appointment.status == "Listo") Icons.Default.CheckCircle else Icons.Default.Schedule

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${appointment.vehicleType}: ${appointment.plate}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(statusIcon, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            appointment.status.uppercase(),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    InfoLabel("CLIENTE", appointment.clientName)
                    InfoLabel("FECHA", appointment.entryDate)
                }
                Column(modifier = Modifier.weight(1f)) {
                    InfoLabel("MECÁNICO", appointment.mechanic)
                    InfoLabel("TOTAL", "$${appointment.totalCost}")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Servicios: ${appointment.selectedServices.joinToString(", ")}",
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun InfoLabel(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(label, color = Color(0xFFD32F2F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 14.sp)
    }
}

fun exportAppointmentsToPdf(context: Context, appointments: List<Appointment>) {
    val pdfDocument = PdfDocument()
    val paint = Paint()
    val titlePaint = Paint()
    
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas

    titlePaint.textSize = 20f
    titlePaint.isFakeBoldText = true
    canvas.drawText("REPORTE DE CITAS - PIT-LANE", 150f, 50f, titlePaint)

    paint.textSize = 12f
    var yPos = 100f

    appointments.forEach { app ->
        if (yPos > 800) return@forEach // Simplificación: solo una página para el ejemplo
        canvas.drawText("ID: ${app.id.take(8)} | Placa: ${app.plate} | Tipo: ${app.vehicleType}", 50f, yPos, paint)
        yPos += 20f
        canvas.drawText("Cliente: ${app.clientName} | Status: ${app.status} | Total: ${app.totalCost}", 50f, yPos, paint)
        yPos += 30f
        canvas.drawLine(50f, yPos - 10f, 550f, yPos - 10f, paint)
    }

    pdfDocument.finishPage(page)

    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, "Reporte_Citas_PitLane_${System.currentTimeMillis()}.pdf")

    try {
        pdfDocument.writeTo(FileOutputStream(file))
        Toast.makeText(context, "PDF Guardado en Descargas", Toast.LENGTH_LONG).show()
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}
