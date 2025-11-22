package com.example.app_clinica_atl.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import com.example.app_clinica_atl.ui.viewmodel.DoctorAgendaItem
import com.example.app_clinica_atl.ui.viewmodel.DoctorScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorScheduleScreen(
    doctorId: Long?,
    onBackClick: () -> Unit,
    viewModel: DoctorScheduleViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(doctorId) {
        doctorId?.let { viewModel.loadAgenda(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Agenda") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (doctorId == null) {
            EmptyState(
                message = "No pudimos identificar tu usuario para cargar la agenda.",
                paddingValues = paddingValues
            )
            return@Scaffold
        }

        when {
            uiState.isLoading -> LoadingState(paddingValues)
            uiState.errorMsg != null -> EmptyState(uiState.errorMsg!!, paddingValues)
            uiState.appointments.isEmpty() -> EmptyState("No tienes citas próximas.", paddingValues)
            else -> AgendaList(
                appointments = uiState.appointments,
                paddingValues = paddingValues,
                cancelingId = uiState.isCancelingId,
                infoMsg = uiState.infoMsg,
                onCancel = { id -> viewModel.cancelAppointment(id) }
            )
        }
    }
}

@Composable
private fun AgendaList(
    appointments: List<DoctorAgendaItem>,
    paddingValues: PaddingValues,
    cancelingId: Long?,
    infoMsg: String?,
    onCancel: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (infoMsg != null) {
            item {
                Text(
                    text = infoMsg,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        items(appointments) { cita ->
            AgendaCard(
                cita = cita,
                isCanceling = cancelingId == cita.appointmentId,
                onCancel = onCancel
            )
        }
    }
}

@Composable
private fun AgendaCard(
    cita: DoctorAgendaItem,
    isCanceling: Boolean,
    onCancel: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Paciente: ${cita.patientName} (ID: ${cita.patientId ?: "-"})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text("Día: ${cita.date}", style = MaterialTheme.typography.bodyLarge)
            Text("Hora: ${cita.time}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Estado:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                StatusBadge(cita.status)
            }
            if (!cita.status.equals("cancelada", true) && !cita.status.equals("completada", true)) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { cita.appointmentId?.let(onCancel) },
                    enabled = cita.appointmentId != null && !isCanceling,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isCanceling) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isCanceling) "Cancelando..." else "Cancelar cita")
                }
            }
        }
    }
}


// todo poder hacer que el doctor pueda cancelar una cita en su agenda
@Composable
private fun StatusBadge(status: String) {
    val normalized = status.lowercase()
    val container = when {
        normalized.contains("cancel") -> MaterialTheme.colorScheme.errorContainer
        normalized.contains("realiz") || normalized.contains("complet") -> MaterialTheme.colorScheme.secondaryContainer
        normalized.contains("confirm") -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when {
        normalized.contains("cancel") -> MaterialTheme.colorScheme.onErrorContainer
        normalized.contains("realiz") || normalized.contains("complet") -> MaterialTheme.colorScheme.onSecondaryContainer
        normalized.contains("confirm") -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = container, contentColor = contentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun LoadingState(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(message: String, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
    }
}
