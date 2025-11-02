package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.R

// --- 1. MODELO DE DATOS ESTÁTICO ---
data class Appointment(
    val id: String,
    val patientName: String,
    val dateTime: String,
    val reason: String
)

@Composable
fun DoctorScheduleScreen(
    modifier: Modifier = Modifier
) {
    // --- 2. DATOS FIJOS DEL DOCTOR VÍCTOR Y SUS CITAS ---
    val doctorName = "Dr. Víctor Rosendo"
    val appointments = remember {
        listOf(
            Appointment("a001", "Lionel Messi", "Hoy, 10:00 - 10:45", "Control de lesión muscular"),
            Appointment("a002", "Keanu Reeves", "Mañana, 14:30 - 15:15", "Seguimiento médico general"),
            Appointment("a003", "Taylor Swift", "Jueves, 09:00 - 09:45", "Revisión de migrañas"),
            Appointment("a004", "Carlos Sainz", "Jueves, 15:00 - 15:45", "Chequeo de rutina")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Clinica",
            modifier = Modifier.height(90.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // TÍTULO CON EL NOMBRE DEL DOCTOR
        Text(
            text = "Horario de Citas de $doctorName",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Próximas ${appointments.size} Citas Agendadas",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(24.dp))

        // LISTA DE CITAS
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(appointments) { appointment ->
                AppointmentCard(appointment = appointment)
            }
        }
    }
}

// --- COMPONENTE: TARJETA DE CITA (Adaptado) ---
@Composable
private fun AppointmentCard(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nombre del Paciente
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Group, contentDescription = "Paciente", tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = appointment.patientName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Fecha y Hora
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Hora", tint = MaterialTheme.colorScheme.secondary)
                Text(
                    text = appointment.dateTime,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Razón/Motivo
            Text(
                text = "Motivo: ${appointment.reason}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DoctorScheduleScreenPreview() {
    DoctorScheduleScreen()
}