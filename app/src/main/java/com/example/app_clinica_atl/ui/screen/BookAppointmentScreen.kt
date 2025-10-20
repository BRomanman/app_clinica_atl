package com.example.app_clinica_atl.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.R
import java.time.LocalDate
import java.time.LocalTime
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
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val doctorOptions = selectedDepartment?.let { DoctorDirectory.doctorsByDepartment[it] }.orEmpty()

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    var selectedDateText by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf<String?>(null) }

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

        // Selección de departamento
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
                    .menuAnchor()
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

        // Selección de doctor
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
                    .menuAnchor()
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
                text = stringResource(id = R.string.book_appointment_doctor_since, it.since),
                style = MaterialTheme.typography.labelMedium
            )
        }

        // INPUT DE FECHA MANUAL (DD/MM/YYYY)
        val dateFieldEnabled = selectedDepartment != null && selectedDoctor != null

        OutlinedTextField(
            value = selectedDateText,
            onValueChange = { raw ->
                if (!dateFieldEnabled) return@OutlinedTextField

                val digits = raw.filter(Char::isDigit).take(8)
                val withSlashes = buildString {
                    for ((i, c) in digits.withIndex()) {
                        append(c)
                        if (i == 1 || i == 3) append('/')
                    }
                }

                selectedDateText = withSlashes
                dateError = null

                if (withSlashes.length == 10) {
                    try {
                        val parsed = LocalDate.parse(withSlashes, dateFormatter)
                        selectedDate = parsed
                        dateError = null
                    } catch (_: Exception) {
                        selectedDate = null
                        dateError = "Fecha inválida. Usa DD/MM/YYYY."
                    }
                } else {
                    selectedDate = null
                }
            },
            readOnly = false,
            enabled = dateFieldEnabled,
            label = { Text(stringResource(id = R.string.book_appointment_date)) },
            placeholder = { Text("DD/MM/YYYY") },
            leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
            isError = dateError != null,
            supportingText = { if (dateError != null) Text(dateError!!) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Selección de hora
        val timeFieldEnabled = selectedDate != null
        ExposedDropdownMenuBox(
            expanded = timeExpanded && timeFieldEnabled,
            onExpandedChange = {
                if (timeFieldEnabled) timeExpanded = !timeExpanded
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
                    .menuAnchor(),
                singleLine = true
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

        // Botón de confirmación
        Button(
            onClick = {
                val department = selectedDepartment
                val doctor = selectedDoctor
                val date = selectedDate
                val time = selectedTime
                if (department != null && doctor != null && date != null && time != null) {
                    onSubmit(AppointmentRequest(department, doctor, date, time))
                    showConfirmationDialog = true
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

        // Diálogo de confirmación
        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                confirmButton = {
                    TextButton(onClick = { showConfirmationDialog = false }) {
                        Text(text = stringResource(id = R.string.common_ok))
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.book_appointment_confirmation_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(text = stringResource(id = R.string.book_appointment_confirmation_body))
                }
            )
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
