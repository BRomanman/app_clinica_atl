package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.notifications.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.LocalTime
import androidx.compose.material3.ButtonDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentScreen(
    onViewProfile: (Long) -> Unit,
    onBookingSuccess: () -> Unit,
    viewModel: BookAppointmentViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            sendAppointmentNotification(context, state.selectedDoctorName, state.selectedDate, state.selectedTime)
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    LaunchedEffect(state.bookingSuccess) {
        if (state.bookingSuccess) {
            maybeSendNotification(context, state, notificationPermissionLauncher::launch)
            onBookingSuccess()
            viewModel.onBookingSuccessHandled()
        }
    }
    LaunchedEffect(state.errorMsg) {
        state.errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // --- Lógica del Calendario ---
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val minDateMillis = calendar.timeInMillis

    if (state.isDatePickerVisible) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= minDateMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = viewModel::hideDatePicker,
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            viewModel.onDateSelected(sdf.format(Date(millis)))
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideDatePicker) { Text("Cancelar") }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
    // --- Fin Lógica Calendario ---

    val cs = MaterialTheme.colorScheme

    val backgroundGradient = remember(
        cs.primaryContainer,
        cs.tertiaryContainer,
        cs.secondaryContainer
    ) {
        Brush.verticalGradient(
            colors = listOf(
                cs.primaryContainer,
                cs.tertiaryContainer,
                cs.secondaryContainer
            )
        )
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Selecciona tu próxima cita",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Agenda con el doctor adecuado y confirma en minutos.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Las agendas se abren desde 2 días en adelante para asegurar disponibilidad.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // 1. Dropdown de Especialidades
                    DropdownMenuField(
                        label = "Especialidad",
                        options = state.specialties,
                        selectedOptionText = state.selectedSpecialty,
                        onOptionSelected = viewModel::onSpecialtyChange,
                        enabled = !state.isBooking && !state.isLoadingSpecialties,
                        isLoading = state.isLoadingSpecialties
                    )
                    Spacer(Modifier.height(12.dp))

                    // 2. Dropdown de Doctores
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownMenuField(
                                label = "Doctor",
                                options = state.doctors.map { it.name },
                                selectedOptionText = state.selectedDoctorName,
                                onOptionSelected = { name ->
                                    val doctor = state.doctors.first { it.name == name }
                                    viewModel.onDoctorChange(doctor)
                                },
                                enabled = !state.isBooking && state.doctors.isNotEmpty(),
                                isLoading = state.isLoadingDoctors
                            )
                        }
                        TextButton(
                            onClick = { state.selectedDoctorUserId?.let { onViewProfile(it) } },
                            enabled = !state.isBooking && state.selectedDoctorUserId != null,
                            modifier = Modifier.padding(start = 8.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary
                            )
                        ) { Text("Ver Perfil") }
                    }
                    Spacer(Modifier.height(12.dp))

                    // --- Campo de Fecha ---
                    OutlinedTextField(
                        value = state.selectedDate,
                        onValueChange = {},
                        label = { Text("Fecha (YYYY-MM-DD)") },
                        placeholder = { Text("Seleccione una fecha") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.selectedDoctorBackendId != null,
                        readOnly = true,
                        isError = state.dateError != null,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = { viewModel.showDatePicker() },
                                enabled = state.selectedDoctorBackendId != null
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Abrir calendario"
                                )
                            }
                        }
                    )
                    state.dateError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.height(12.dp))

                    // 4. Dropdown de Horas
                    DropdownMenuField(
                        label = "Hora",
                        options = state.availableTimes,
                        selectedOptionText = state.selectedTime,
                        onOptionSelected = viewModel::onTimeChange,
                        enabled = !state.isBooking && state.availableTimes.isNotEmpty(),
                        isLoading = state.isLoadingTimes,
                        isError = state.timeError != null
                    )
                    if (!state.isLoadingTimes && state.selectedDate.isNotBlank() && state.availableTimes.isEmpty()) {
                        Text(
                            text = "No hay horarios disponibles para la fecha seleccionada.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    state.timeError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.height(16.dp))

                    // 5. Botón de Enviar
                    Button(
                        onClick = viewModel::submitBooking,
                        enabled = !state.isBooking && state.selectedTime.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (state.isBooking) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Agendando...")
                        } else {
                            Text("Confirmar Cita")
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownMenuField(
    label: String,
    options: List<String>,
    selectedOptionText: String,
    onOptionSelected: (String) -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled && !isLoading) expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                disabledBorderColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            enabled = enabled,
            isError = isError,
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        // --- ¡¡AQUÍ ESTÁ LA CORRECCIÓN!! ---
                        onOptionSelected(selectionOption) // Era selectionD
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun maybeSendNotification(
    context: android.content.Context,
    state: com.example.app_clinica_atl.ui.viewmodel.BookAppointmentUiState,
    requestPermission: (String) -> Unit
) {
    if (!state.bookingSuccess || state.selectedDate.isBlank() || state.selectedTime.isBlank()) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    NotificationHelper.createNotificationChannel(context)
    val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

    if (needsPermission) {
        requestPermission(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        sendAppointmentNotification(context, state.selectedDoctorName, state.selectedDate, state.selectedTime)
    }
}

private fun sendAppointmentNotification(
    context: android.content.Context,
    doctorName: String,
    dateStr: String,
    timeStr: String
) {
    val date = runCatching { LocalDate.parse(dateStr) }.getOrNull() ?: return
    val time = runCatching { LocalTime.parse(timeStr) }.getOrNull() ?: return
    NotificationHelper.showAppointmentConfirmation(
        context = context,
        doctorName = doctorName.ifBlank { "Tu doctor" },
        date = date,
        time = time
    )
}
