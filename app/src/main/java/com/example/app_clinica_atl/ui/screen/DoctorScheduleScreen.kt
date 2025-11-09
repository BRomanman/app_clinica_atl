package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel

// --- 1. COMPOSABLE "INTELIGENTE" (CON VM) ---
@Composable
fun DoctorScheduleScreenVm(
    vm: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val doctorInfo by vm.currentDoctorInfo.collectAsStateWithLifecycle()
    val appointments by vm.doctorAppointments.collectAsStateWithLifecycle()

    DoctorScheduleScreen(
        doctorInfo = doctorInfo,
        appointments = appointments,
        onCancelAppointment = vm::cancelAppointment,
        modifier = modifier
    )
}

// --- 2. COMPOSABLE "TONTO" (SOLO UI) ---
@Composable
private fun DoctorScheduleScreen(
    doctorInfo: DoctorInfo?,
    appointments: List<AppointmentEntity>,
    onCancelAppointment: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado para el diálogo de confirmación
    var showCancelDialog by remember { mutableStateOf<Long?>(null) }

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

        // Muestra "Cargando..." o el nombre real del doctor
        if (doctorInfo == null) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cargando agenda...",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Text(
                text = "Agenda de ${doctorInfo.name}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Próximas ${appointments.size} Citas Agendadas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // LISTA DE CITAS
        if (appointments.isEmpty() && doctorInfo != null) {
            Text(
                text = "No tienes citas agendadas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(appointments, key = { it.id }) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onCancelClick = {
                            showCancelDialog = appointment.id
                        }
                    )
                }
            }
        }
    }

    // --- 3. DIÁLOGO DE CONFIRMACIÓN PARA CANCELAR ---
    if (showCancelDialog != null) {
        val appointmentIdToCancel = showCancelDialog!!
        AlertDialog(
            onDismissRequest = { showCancelDialog = null },
            title = { Text("Cancelar Cita") },
            text = { Text("¿Estás seguro de que quieres cancelar esta cita con el paciente?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancelAppointment(appointmentIdToCancel)
                        showCancelDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirmar Cancelación")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = null }) {
                    Text("Atrás")
                }
            }
        )
    }
}

// --- 3. COMPONENTE: TARJETA DE CITA (Adaptado para mostrar paciente) ---
@Composable
private fun AppointmentCard(
    appointment: AppointmentEntity,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Nombre del Paciente
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Group, contentDescription = "Paciente", tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = appointment.patientName, // <-- DATO REAL
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Fecha y Hora
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Fecha",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.date, // <-- DATO REAL
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Hora",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.time, // <-- DATO REAL
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botón de Cancelar
            IconButton(
                onClick = onCancelClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Cancelar Cita",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DoctorScheduleScreenPreview() {
    // Preview con datos falsos solo para la UI
    val previewDoctor = DoctorInfo(firstName = "Dr. Víctor", lastName = "Rosendo")
    val previewAppointments = listOf(
        AppointmentEntity(id = 1, patientId = 1, patientName = "Lionel Messi", doctorId = "000", doctorName = "V.R", department = "Cardio", date = "2025-11-10", time = "10:00"),
        AppointmentEntity(id = 2, patientId = 2, patientName = "Keanu Reeves", doctorId = "000", doctorName = "V.R", department = "Cardio", date = "2025-11-10", time = "11:00")
    )

    DoctorScheduleScreen(
        doctorInfo = previewDoctor,
        appointments = previewAppointments,
        onCancelAppointment = {}
    )
}