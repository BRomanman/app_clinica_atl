package com.example.app_clinica_atl.ui.screen.admin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.notifications.NotificationHelper
import com.example.app_clinica_atl.ui.screen.patient.formatDateInput
import com.example.app_clinica_atl.ui.viewmodel.admin.AdminAddDoctorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddDoctorScreen(
    viewModel: AdminAddDoctorViewModel,
    onBackClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            uiState.createdDoctorName?.let { NotificationHelper.showDoctorCreated(context, it) }
        }
    }

    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            maybeSendDoctorCreatedNotification(
                context = context,
                doctorName = uiState.createdDoctorName,
                requestPermission = notificationPermissionLauncher::launch
            )
            Toast.makeText(
                context,"¡Doctor registrado con éxito!",Toast.LENGTH_LONG).show()
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nuevo Doctor") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
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

            // --------- Datos personales ----------
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = viewModel::onFirstNameChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.firstNameError != null,
                singleLine = true
            )
            uiState.firstNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = viewModel::onLastNameChange,
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.lastNameError != null,
                singleLine = true
            )
            uiState.lastNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(8.dp))




            var birthField by remember(uiState.birthDate) {
                mutableStateOf(TextFieldValue(uiState.birthDate, selection = TextRange(uiState.birthDate.length)))
            }
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
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.emailError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            uiState.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Telefono (+56912345678)") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.phoneError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            uiState.phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(8.dp))

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

            Spacer(Modifier.height(16.dp))

            // --------- Especialidades ----------
            Text(
                "Especialidades",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            uiState.specialtiesError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(4.dp))

            // Lista de especialidades del backend
            if (uiState.backendSpecialties.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    items(
                        items = uiState.backendSpecialties,
                        key = { it.id }
                    ) { spec: EspecialidadDto ->
                        val checked = uiState.selectedSpecialties.contains(spec.name)
                        SpecialtyCheckRow(
                            label = spec.name,
                            checked = checked,
                            onToggle = { viewModel.toggleBackendSpecialty(spec) }
                        )
                        Divider()
                    }
                }
            } else {
                Text("No hay especialidades registradas aún.", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(16.dp))

            // --------- Botón Registrar ----------
            Button(
                onClick = viewModel::registerDoctor,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Registrar Doctor")
                }
            }

            // Mensajes
            uiState.errorMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            if (uiState.registrationSuccess) {
                Spacer(Modifier.height(8.dp))
                Text("¡Doctor registrado con éxito!", color = MaterialTheme.colorScheme.primary)
            }
        }
    }

}

@Composable
private fun SpecialtyCheckRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() }
        )
    }
}

private fun maybeSendDoctorCreatedNotification(
    context: Context,
    doctorName: String?,
    requestPermission: (String) -> Unit
) {
    if (doctorName.isNullOrBlank()) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    NotificationHelper.createNotificationChannel(context)
    val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

    if (needsPermission) {
        requestPermission(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        NotificationHelper.showDoctorCreated(context, doctorName)
    }
}
