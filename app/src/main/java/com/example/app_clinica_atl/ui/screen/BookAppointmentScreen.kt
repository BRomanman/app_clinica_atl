package com.example.app_clinica_atl.ui.screen

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_clinica_atl.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel


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

    LaunchedEffect(state.bookingSuccess) {
        if (state.bookingSuccess) {
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
    calendar.add(Calendar.DAY_OF_YEAR, 3)
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Selecciona tu próxima cita",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(20.dp))

            // 1. Dropdown de Especialidades
            DropdownMenuField(
                label = "Especialidad",
                options = state.specialties,
                selectedOptionText = state.selectedSpecialty,
                onOptionSelected = viewModel::onSpecialtyChange,
                enabled = !state.isBooking
            )
            Spacer(Modifier.height(8.dp))

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
                    onClick = { state.selectedDoctorId?.let { onViewProfile(it) } },
                    enabled = !state.isBooking && state.selectedDoctorId != null,
                    modifier = Modifier.padding(start = 8.dp)
                ) { Text("Ver Perfil") }
            }
            Spacer(Modifier.height(8.dp))

            // --- Campo de Fecha ---
            OutlinedTextField(
                value = state.selectedDate,
                onValueChange = {},
                label = { Text("Fecha (YYYY-MM-DD)") },
                placeholder = { Text("Seleccione una fecha") },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.selectedDoctorId != null,
                readOnly = true,
                isError = state.dateError != null,
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.showDatePicker() },
                        enabled = state.selectedDoctorId != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Abrir calendario"
                        )
                    }
                }
            )
            state.dateError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(8.dp))

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
            state.timeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(16.dp))

            // 5. Botón de Enviar
            Button(
                onClick = viewModel::submitBooking,
                enabled = !state.isBooking && state.selectedTime.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
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
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            enabled = enabled,
            isError = isError
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