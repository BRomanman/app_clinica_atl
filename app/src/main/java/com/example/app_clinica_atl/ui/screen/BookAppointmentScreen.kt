package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel

/**
 * Pantalla de Agendar Cita
 * ¡SIN TopBar y SIN 'onBackClick'!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentScreen(
    onViewProfile: (Long) -> Unit,
    viewModel: BookAppointmentViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val bg = MaterialTheme.colorScheme.tertiaryContainer
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMsg, state.bookingSuccess) {
        state.errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearBookingResult()
        }
        if (state.bookingSuccess) {
            snackbarHostState.showSnackbar("¡Cita agendada con éxito!")
            viewModel.clearBookingResult()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bg
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
                    onClick = {
                        state.selectedDoctorId?.let { onViewProfile(it) }
                    },
                    enabled = !state.isBooking && state.selectedDoctorId != null,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Ver Perfil")
                }
            }
            Spacer(Modifier.height(8.dp))

            // 3. Selector de Fecha
            OutlinedTextField(
                value = state.selectedDate,
                onValueChange = viewModel::onDateChange,
                label = { Text("Fecha (YYYY-MM-DD)") },
                placeholder = { Text("Ej: 2025-12-25") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isBooking && state.selectedDoctorId != null,
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            // 4. Dropdown de Horas
            DropdownMenuField(
                label = "Hora",
                options = state.availableTimes,
                selectedOptionText = state.selectedTime,
                onOptionSelected = viewModel::onTimeChange,
                enabled = !state.isBooking && state.availableTimes.isNotEmpty(),
                isLoading = state.isLoadingTimes
            )
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
                        onOptionSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}