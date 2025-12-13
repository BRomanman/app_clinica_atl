package com.example.app_clinica_atl.ui.screen.doctor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.ui.viewmodel.doctor.DoctorPatientProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorPatientProfileScreen(
    patientId: Long,
    onBackClick: () -> Unit,
    viewModel: DoctorPatientProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(patientId) {
        viewModel.loadPatient(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Perfil de paciente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (uiState.isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
                return@LazyColumn
            }

            uiState.patient?.let { patient ->
                item { PersonalInfoCard(patient) }
            }

            item { SectionTitle("Citas del paciente") }
            when {
                uiState.appointments.isNotEmpty() -> {
                    items(uiState.appointments) { cita ->
                        AppointmentRow(
                            cita = cita,
                            isCancelling = uiState.isCancellingId == cita.id,
                            isFinishing = uiState.isFinishingId == cita.id,
                            onCancel = { cita.id?.let(viewModel::cancelAppointment) },
                            onFinish = { cita.id?.let(viewModel::finalizeAppointment) }
                        )
                    }
                }
                uiState.errorMsg?.contains("citas", true) == true -> {
                    item { EmptyState(uiState.errorMsg ?: "No fue posible cargar las citas del paciente.") }
                }
                else -> item { EmptyState("Este paciente no tiene citas programadas con este doctor.") }
            }

            item { SectionTitle("Seguros") }
            when {
                uiState.insurances.isNotEmpty() -> {
                    items(uiState.insurances) { seguro ->
                        InsuranceRow(seguro)
                    }
                }
                uiState.errorMsg?.contains("seguro", true) == true -> {
                    item { EmptyState(uiState.errorMsg ?: "No fue posible cargar los seguros.") }
                }
                else -> item { EmptyState("Sin seguros asociados.") }
            }

            item { SectionTitle("Historial médico") }
            when {
                uiState.histories.isNotEmpty() -> {
                    items(uiState.histories) { hist -> HistoryRow(hist) }
                }
                uiState.errorMsg?.contains("historial", true) == true -> {
                    item { EmptyState(uiState.errorMsg ?: "No fue posible cargar el historial médico.") }
                }
                else -> item { EmptyState("No hay registros de historial para este paciente.") }
            }

            if (uiState.patient == null) {
                uiState.errorMsg?.let { msg ->
                    item { Text(msg, color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoCard(patient: UsuarioDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                patient.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                patient.email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Teléfono: ${patient.phone}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "ID Usuario: ${patient.id}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AppointmentRow(
    cita: CitaDto,
    isCancelling: Boolean,
    isFinishing: Boolean,
    onCancel: () -> Unit,
    onFinish: () -> Unit
) {
    val statusNormalized = cita.estado.uppercase()
    val isRealizada = statusNormalized == "REALIZADA"
    val isCancelada = statusNormalized == "CANCELADA"
    val showActions = !isRealizada && !isCancelada
    val containerColor = if (isRealizada) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val statusColor = when {
        isRealizada -> MaterialTheme.colorScheme.secondary
        isCancelada -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Fecha: ${cita.date}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Hora: ${cita.time}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Doctor ID: ${cita.doctorId}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Estado: ${cita.estado}",
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor
            )
            if (showActions) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onCancel,
                        enabled = !isCancelling && !isFinishing,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(if (isCancelling) "Cancelando..." else "Cancelar cita")
                    }
                    Button(
                        onClick = onFinish,
                        enabled = !isFinishing && !isCancelling,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(if (isFinishing) "Finalizando..." else "Finalizar cita")
                    }
                }
            }
        }
    }
}

@Composable
private fun InsuranceRow(seguro: SeguroDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(seguro.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(seguro.description, style = MaterialTheme.typography.bodyMedium)
            Text("Precio: ${seguro.price}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun HistoryRow(hist: HistorialDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = hist.fechaConsulta?.takeIf { it.isNotBlank() } ?: "Sin fecha",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Diagnóstico",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = hist.diagnostico?.takeIf { it.isNotBlank() } ?: "Sin diagnóstico",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Observaciones",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = hist.observaciones?.takeIf { it.isNotBlank() } ?: "Sin observaciones",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun EmptyState(message: String) {
    Text(message, style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(4.dp))
}
