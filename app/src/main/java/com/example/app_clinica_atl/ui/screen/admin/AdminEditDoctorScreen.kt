package com.example.app_clinica_atl.ui.screen.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.ui.screen.patient.formatDateInput
import com.example.app_clinica_atl.ui.viewmodel.admin.AdminEditDoctorViewModel

/**
 * Pantalla de edicion de doctor. Muestra datos basicos, remuneraciones y especialidad.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditDoctorScreen(
    viewModel: AdminEditDoctorViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var specialtyExpanded by remember { mutableStateOf(false) }
    var birthField by remember(uiState.birthDate) {
        mutableStateOf(TextFieldValue(uiState.birthDate, selection = TextRange(uiState.birthDate.length)))
    }

    LaunchedEffect(uiState.birthDate) {
        birthField = TextFieldValue(uiState.birthDate, selection = TextRange(uiState.birthDate.length))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Doctor") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.isLoading && uiState.doctorId == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                return@Column
            }

            Text(
                text = "ID doctor: ${uiState.doctorId ?: "-"}",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.nombreError != null
            )
            uiState.nombreError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(
                value = uiState.apellido,
                onValueChange = viewModel::onApellidoChange,
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.apellidoError != null
            )
            uiState.apellidoError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = uiState.emailError != null
            )
            uiState.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(
                value = uiState.telefono,
                onValueChange = viewModel::onTelefonoChange,
                label = { Text("Telefono (+569########)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                isError = uiState.telefonoError != null
            )
            uiState.telefonoError?.let { Text(it, color = MaterialTheme.colorScheme.error) }









            OutlinedTextField(
                value = birthField,
                onValueChange = { newValue ->
                    val formatted = formatDateInput(newValue.text)
                    val tfv = TextFieldValue(formatted, selection = TextRange(formatted.length))
                    birthField = tfv
                    viewModel.onBirthDateChange(formatted)
                },
                label = { Text("Fecha Nacimiento (dd-mm-yyyy)") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.birthDateError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            uiState.birthDateError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }










            OutlinedTextField(
                value = uiState.tarifaConsulta,
                onValueChange = viewModel::onTarifaChange,
                label = { Text("Tarifa consulta (CLP)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.tarifaError != null
            )
            uiState.tarifaError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(
                value = uiState.sueldo,
                onValueChange = viewModel::onSueldoChange,
                label = { Text("Salario (CLP)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.sueldoError != null
            )
            uiState.sueldoError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(
                value = uiState.bono,
                onValueChange = viewModel::onBonoChange,
                label = { Text("Bono (CLP)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.bonoError != null
            )
            uiState.bonoError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            ExposedDropdownMenuBox(
                expanded = specialtyExpanded,
                onExpandedChange = { specialtyExpanded = !specialtyExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.selectedSpecialtyName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especialidad") },
                    placeholder = { Text("Seleccione una especialidad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = specialtyExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = uiState.specialtyError != null
                )
                ExposedDropdownMenu(
                    expanded = specialtyExpanded,
                    onDismissRequest = { specialtyExpanded = false }
                ) {
                    uiState.backendSpecialties.forEach { specialty ->
                        DropdownMenuItem(
                            text = { Text(specialty.name) },
                            onClick = {
                                viewModel.onSpecialtySelected(specialty)
                                specialtyExpanded = false
                            }
                        )
                    }
                }
            }
            uiState.specialtyError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = viewModel::saveChanges,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth(),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar cambios")
                }
            }

            uiState.errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (uiState.updateSuccess) {
                Text("Datos actualizados correctamente.", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
