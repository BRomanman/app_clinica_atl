package com.example.app_clinica_atl.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.ui.theme.AppClinicaATLTheme
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentUiState
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// 1. Composable "Inteligente"
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookAppointmentScreenVm(vm: BookAppointmentViewModel) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    BookAppointmentScreen(
        state = uiState,
        onDepartmentSelected = vm::onDepartmentSelected,
        onDoctorSelected = vm::onDoctorSelected,
        onDateSelected = vm::onDateSelected, // Para DatePicker
        onTimeSelected = vm::onTimeSelected,
        onDepartmentExpandedChange = vm::onDepartmentExpandedChange,
        onDoctorExpandedChange = vm::onDoctorExpandedChange,
        onTimeExpandedChange = vm::onTimeExpandedChange,
        onShowDatePicker = vm::showDatePicker, // Para abrir/cerrar DatePicker
        onSubmit = vm::submitAppointment,
        onDismissConfirmation = vm::dismissConfirmationDialog
    )
}

// 2. Composable "Tonto"
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookAppointmentScreen(
    state: BookAppointmentUiState,
    onDepartmentSelected: (String) -> Unit,
    onDoctorSelected: (DoctorInfo) -> Unit,
    onDateSelected: (Long?) -> Unit, // Recibe millis del DatePicker
    onTimeSelected: (LocalTime) -> Unit,
    onDepartmentExpandedChange: (Boolean) -> Unit,
    onDoctorExpandedChange: (Boolean) -> Unit,
    onTimeExpandedChange: (Boolean) -> Unit,
    onShowDatePicker: (Boolean) -> Unit, // Lambda para mostrar/ocultar DatePicker
    onSubmit: () -> Unit,
    onDismissConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Formateadores para mostrar fecha y hora
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    // Estado para el DatePickerDialog
    val datePickerState = rememberDatePickerState()

    // --- DatePickerDialog ---
    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onShowDatePicker(false) },
            confirmButton = {
                TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                    Text(text = stringResource(id = R.string.common_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowDatePicker(false) }) {
                    Text(text = stringResource(id = R.string.common_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- Contenido Principal ---
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.book_appointment_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(stringResource(id = R.string.book_appointment_subtitle), style = MaterialTheme.typography.bodyMedium)

        // --- Departamento ---
        ExposedDropdownMenuBox(expanded = state.departmentExpanded, onExpandedChange = onDepartmentExpandedChange) {
            OutlinedTextField(
                value = state.selectedDepartment.orEmpty(), onValueChange = {}, readOnly = true,
                label = { Text(stringResource(id = R.string.book_appointment_department)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.departmentExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = state.departmentExpanded, onDismissRequest = { onDepartmentExpandedChange(false) }) {
                state.departments.forEach { department ->
                    DropdownMenuItem(text = { Text(department) }, onClick = { onDepartmentSelected(department) })
                }
            }
        }

        // --- Doctor ---
        ExposedDropdownMenuBox(expanded = state.doctorExpanded, onExpandedChange = onDoctorExpandedChange) {
            OutlinedTextField(
                value = state.selectedDoctor?.name.orEmpty(), onValueChange = {}, readOnly = true,
                label = { Text(stringResource(id = R.string.book_appointment_doctor)) },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.doctorExpanded) },
                enabled = state.doctors.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = state.doctorExpanded, onDismissRequest = { onDoctorExpandedChange(false) }) {
                state.doctors.forEach { doctor ->
                    DropdownMenuItem(text = { Text("${doctor.name} - ${doctor.specialty}") }, onClick = { onDoctorSelected(doctor) })
                }
            }
        }
        state.selectedDoctor?.let { Text(stringResource(id = R.string.book_appointment_doctor_since, it.since), style = MaterialTheme.typography.labelMedium) }








        val dateFieldEnabled = state.selectedDepartment != null && state.selectedDoctor != null

// Ya no necesitas 'dateClickInteraction' para esto

        ExposedDropdownMenuBox(
            // 1. Vincula el estado "expanded" a la visibilidad de tu DatePicker
            expanded = state.showDatePicker,
            // 2. Usa onExpandedChange como tu nuevo 'onClick'
            onExpandedChange = {
                // 'it' será 'true' cuando se haga clic para abrir
                if (dateFieldEnabled) {
                    onShowDatePicker(it)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.selectedDate?.format(dateFormatter).orEmpty(),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(id = R.string.book_appointment_date)) },
                placeholder = { Text(stringResource(id = R.string.book_appointment_date_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Event, null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.showDatePicker) },
                enabled = dateFieldEnabled,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(), // Opcional, pero recomendado
                modifier = Modifier
                    .fillMaxWidth()

                    //    Le dice al Box que ESTE campo es el ancla que debe ser clicable.
                    .menuAnchor(),
                singleLine = true
            )


            //    El DatePicker es un Dialog, así que no mostramos nada aquí.
            //    Esto te da un onDismissRequest (clic fuera) gratis.
            ExposedDropdownMenu(
                expanded = state.showDatePicker,
                onDismissRequest = { onShowDatePicker(false) }
            ) {
                // No se necesita contenido aquí para el DatePicker
            }
        }







        // --- Hora ---
        val timeFieldEnabled = state.selectedDate != null
        ExposedDropdownMenuBox(expanded = state.timeExpanded && timeFieldEnabled, onExpandedChange = onTimeExpandedChange) {
            OutlinedTextField(
                value = state.selectedTime?.format(timeFormatter).orEmpty(), onValueChange = {}, readOnly = true,
                label = { Text(stringResource(id = R.string.book_appointment_time)) },
                leadingIcon = { Icon(Icons.Default.AccessTime, null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.timeExpanded && timeFieldEnabled) },
                enabled = timeFieldEnabled, singleLine = true,
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = state.timeExpanded && timeFieldEnabled, onDismissRequest = { onTimeExpandedChange(false) }) {
                state.availableTimes.forEach { time ->
                    DropdownMenuItem(text = { Text(time.format(timeFormatter)) }, onClick = { onTimeSelected(time) })
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Botón Submit ---
        Button(
            onClick = onSubmit,
            enabled = state.selectedDepartment != null && state.selectedDoctor != null && state.selectedDate != null && state.selectedTime != null && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isSubmitting) CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            else Text(stringResource(id = R.string.book_appointment_submit))
        }
        state.submissionError?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }

        // --- Diálogo de Confirmación ---
        if (state.showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = onDismissConfirmation,
                confirmButton = { TextButton(onClick = onDismissConfirmation) { Text(stringResource(id = R.string.common_ok)) } },
                title = { Text(stringResource(id = R.string.book_appointment_confirmation_title), fontWeight = FontWeight.Bold) },
                text = { Text(stringResource(id = R.string.book_appointment_confirmation_body)) }
            )
        }
    }
}

// 3. Preview (Se mantiene funcional)
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun BookAppointmentScreenPreview() {
    val previewState = BookAppointmentUiState(
        departments = listOf("Medicina General", "Cardiología"),
        availableTimes = listOf(LocalTime.of(9,0), LocalTime.of(10,30))
    )
    AppClinicaATLTheme {
        BookAppointmentScreen(
            state = previewState, onDepartmentSelected = {}, onDoctorSelected = {},
            onDateSelected = {}, onTimeSelected = {}, onDepartmentExpandedChange = {},
            onDoctorExpandedChange = {}, onTimeExpandedChange = {}, onShowDatePicker = {},
            onSubmit = {}, onDismissConfirmation = {}
        )
    }
}
