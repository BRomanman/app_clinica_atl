package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.ui.viewmodel.AdminAddDoctorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddDoctorScreen(
    viewModel: AdminAddDoctorViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    // Muestra mensaje de éxito
    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            // (Aquí podríamos mostrar un Snackbar)
            viewModel.clearSuccess() // Resetea el estado
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nuevo Doctor") },
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
                .verticalScroll(rememberScrollState())
        ) {
            // --- Formulario de Registro ---
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.nameError != null,
                singleLine = true
            )
            uiState.nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.emailError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            uiState.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.phoneError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            uiState.phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.passwordError != null,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            uiState.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.confirmPasswordError != null,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            uiState.confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.salary,
                onValueChange = viewModel::onSalaryChange,
                label = { Text("Salario (Ej: 2500000)") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.salaryError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            uiState.salaryError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(modifier = Modifier.height(8.dp))

            // --- ¡¡EL COMBO BOX (DROPDOWN)!! ---
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = uiState.selectedSpecialty?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especialidad") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = uiState.specialtyError != null
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (uiState.isLoading) {
                        DropdownMenuItem(
                            text = { Text("Cargando...") },
                            onClick = { }
                        )
                    }
                    uiState.specialties.forEach { specialty ->
                        DropdownMenuItem(
                            text = { Text("${specialty.name} (\$${specialty.price.toInt()})") },
                            onClick = {
                                viewModel.onSpecialtyChange(specialty)
                                expanded = false
                            }
                        )
                    }
                }
            }
            uiState.specialtyError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            // --- FIN DEL COMBO BOX ---

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::registerDoctor,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(24.dp))
                } else {
                    Text("Registrar Doctor")
                }
            }

            uiState.errorMsg?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
            if (uiState.registrationSuccess) {
                Text("¡Doctor registrado con éxito!", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}