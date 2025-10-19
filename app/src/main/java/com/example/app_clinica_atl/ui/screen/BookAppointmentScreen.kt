package com.example.app_clinica_atl.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MenuAnchorType
import androidx.compose.ui.tooling.preview.Preview
import com.example.app_clinica_atl.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


data class AppointmentRequest(
    val department: String,
    val doctor: DoctorInfo,
    val date: LocalDate,
    val time: LocalTime
)

data class DoctorInfo(
    val name: String,
    val specialty: String,
    val since: Int
)

private object DoctorDirectory {
    val departments = listOf(
        "Medicina General",
        "Cardiología",
        "Dermatología",
        "Pediatría",
        "Psicología",
        "Nutrición"
    )

    val doctorsByDepartment = mapOf(
        "Medicina General" to listOf(
            DoctorInfo("Dra. Ana Pérez", "Traumatóloga", 2012),
            DoctorInfo("Dra. Juana Pérez", "Médico de familia", 2010),
            DoctorInfo("Dra. Marcela Ruiz", "Ginecóloga", 2015),
            DoctorInfo("Dra. Alejandra Peña", "Médico de atención primaria", 2011),
            DoctorInfo("Dr. Ignacio Fuentes", "Traumatólogo", 2013)
        ),
        "Cardiología" to listOf(
            DoctorInfo("Dr. Juan Torres", "Cardiólogo", 2012),
            DoctorInfo("Dra. Marcela Ruiz", "Cardióloga", 2015),
            DoctorInfo("Dra. Ricarda Gómez", "Cardióloga", 2016),
            DoctorInfo("Dra. Valentina Castro", "Cardióloga", 2013)
        ),
        "Dermatología" to listOf(
            DoctorInfo("Dra. Ana Pérez", "Dermatóloga", 2011),
            DoctorInfo("Dr. Nicolás Díaz", "Dermatólogo", 2012),
            DoctorInfo("Dra. Isabel Soto", "Dermatóloga", 2014),
            DoctorInfo("Dr. Paulo Bravo", "Dermatólogo", 2013),
            DoctorInfo("Dra. Lorena Salazar", "Dermatóloga", 2015)
        ),
        "Pediatría" to listOf(
            DoctorInfo("Dr. Gabriel Molina", "Pediatra", 2010),
            DoctorInfo("Dra. Fernanda Morales", "Pediatra", 2011),
            DoctorInfo("Dra. Natalia Carrasco", "Pediatra", 2012)
        ),
        "Psicología" to listOf(
            DoctorInfo("Dr. Sebastián Flores", "Psicólogo", 2013),
            DoctorInfo("Dra. Catalina Reyes", "Psicóloga", 2014),
            DoctorInfo("Dr. Esteban Rivas", "Psicólogo", 2015),
            DoctorInfo("Dr. Marcelo Duarte", "Psicólogo", 2012)
        ),
        "Nutrición" to listOf(
            DoctorInfo("Dra. Verónica Contreras", "Nutrióloga", 2011),
            DoctorInfo("Dr. Felipe Lagos", "Nutriólogo", 2012)
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentScreen(
    modifier: Modifier = Modifier,
    onSubmit: (AppointmentRequest) -> Unit = {}
) {
    var departmentExpanded by remember { mutableStateOf(false) }
    var doctorExpanded by remember { mutableStateOf(false) }

    var selectedDepartment by remember { mutableStateOf<String?>(null) }
    var selectedDoctor by remember { mutableStateOf<DoctorInfo?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    val doctorOptions = selectedDepartment?.let { DoctorDirectory.doctorsByDepartment[it] }.orEmpty()

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("dd MMM yyyy")
    }
    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("HH:mm")
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val availableTimes = remember {
        listOf(
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 30),
            LocalTime.of(12, 30),
            LocalTime.of(14, 0),
            LocalTime.of(15, 30),
            LocalTime.of(17, 0)
        )
    }
    var timeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDepartment) {
        if (selectedDoctor != null && selectedDoctor !in doctorOptions) {
            selectedDoctor = null
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.common_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = stringResource(id = R.string.common_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.book_appointment_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.book_appointment_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )

        ExposedDropdownMenuBox(
            expanded = departmentExpanded,
            onExpandedChange = { departmentExpanded = !departmentExpanded }
        ) {
            OutlinedTextField(
                value = selectedDepartment.orEmpty(),
                onValueChange = {},
                readOnly = true,
                label = { Text(text = stringResource(id = R.string.book_appointment_department)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = departmentExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = departmentExpanded,
                onDismissRequest = { departmentExpanded = false }
            ) {
                DoctorDirectory.departments.forEach { department ->
                    DropdownMenuItem(
                        text = { Text(department) },
                        onClick = {
                            selectedDepartment = department
                            departmentExpanded = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = doctorExpanded,
            onExpandedChange = {
                if (doctorOptions.isNotEmpty()) {
                    doctorExpanded = !doctorExpanded
                }
            }
        ) {
            OutlinedTextField(
                value = selectedDoctor?.name.orEmpty(),
                onValueChange = {},
                readOnly = true,
                label = { Text(text = stringResource(id = R.string.book_appointment_doctor)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorExpanded) },
                enabled = doctorOptions.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = doctorOptions.isNotEmpty()
                    )
            )
            ExposedDropdownMenu(
                expanded = doctorExpanded,
                onDismissRequest = { doctorExpanded = false }
            ) {
                doctorOptions.forEach { doctor ->
                    DropdownMenuItem(
                        text = { Text("${doctor.name} - ${doctor.specialty}") },
                        onClick = {
                            selectedDoctor = doctor
                            doctorExpanded = false
                        }
                    )
                }
            }
        }

        selectedDoctor?.let {
            Text(
                text = stringResource(
                    id = R.string.book_appointment_doctor_since,
                    it.since
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }

        val dateFieldEnabled = selectedDepartment != null && selectedDoctor != null
        val dateInteractionSource = remember { MutableInteractionSource() }
        OutlinedTextField(
            value = selectedDate?.format(dateFormatter).orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(id = R.string.book_appointment_date)) },
            placeholder = { Text(text = stringResource(id = R.string.book_appointment_date_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDatePicker) },
            enabled = dateFieldEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .let { base ->
                    if (dateFieldEnabled) {
                        base.clickable(
                            interactionSource = dateInteractionSource,
                            indication = null
                        ) { showDatePicker = true }
                    } else {
                        base
                    }
                },
            interactionSource = dateInteractionSource,
            singleLine = true
        )

        val timeFieldEnabled = selectedDate != null
        ExposedDropdownMenuBox(
            expanded = timeExpanded && timeFieldEnabled,
            onExpandedChange = {
                if (timeFieldEnabled) {
                    timeExpanded = !timeExpanded
                }
            }
        ) {
            OutlinedTextField(
                value = selectedTime?.format(timeFormatter).orEmpty(),
                onValueChange = {},
                readOnly = true,
                label = { Text(text = stringResource(id = R.string.book_appointment_time)) },
                placeholder = { Text(text = stringResource(id = R.string.book_appointment_time_placeholder)) },
                leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeExpanded && timeFieldEnabled) },
                enabled = timeFieldEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = timeFieldEnabled
                    )
                ,singleLine = true
            )

            ExposedDropdownMenu(
                expanded = timeExpanded && timeFieldEnabled,
                onDismissRequest = { timeExpanded = false }
            ) {
                availableTimes.forEach { time ->
                    DropdownMenuItem(
                        text = { Text(time.format(timeFormatter)) },
                        onClick = {
                            selectedTime = time
                            timeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val department = selectedDepartment
                val doctor = selectedDoctor
                val date = selectedDate
                val time = selectedTime
                if (department != null && doctor != null && date != null && time != null) {
                    onSubmit(AppointmentRequest(department, doctor, date, time))
                }
            },
            enabled = selectedDepartment != null &&
                selectedDoctor != null &&
                selectedDate != null &&
                selectedTime != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.book_appointment_submit))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun BookAppointmentScreenPreview() {
    BookAppointmentScreen(
        onSubmit = { appointment ->
            println("Appointment submitted: $appointment")
        }
    )
}
