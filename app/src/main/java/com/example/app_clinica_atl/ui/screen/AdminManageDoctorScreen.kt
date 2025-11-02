package com.example.app_clinica_atl.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.ui.viewmodel.AdminManageDoctorUiState
import com.example.app_clinica_atl.ui.viewmodel.AdminManageDoctorViewModel

@Composable
fun AdminManageDoctorScreenVm(
    vm: AdminManageDoctorViewModel,
    modifier: Modifier = Modifier
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    AdminManageDoctorScreen(
        state = state,
        onDoctorIdChange = vm::onDoctorIdChange,
        onSearchDoctor = vm::searchDoctor,
        onFirstNameChange = vm::onFirstNameChange,
        onLastNameChange = vm::onLastNameChange,
        onBirthDateChange = vm::onBirthDateChange,
        onEmailChange = vm::onEmailChange,
        onContactNumberChange = vm::onContactNumberChange,
        onPasswordChange = vm::onPasswordChange,
        onConsultationRateChange = vm::onConsultationRateChange,
        onSalaryChange = vm::onSalaryChange,
        onBonusChange = vm::onBonusChange,
        onSpecialtyIdChange = vm::onSpecialtyIdChange,
        onSpecialtyChange = vm::onSpecialtyChange,
        onAvailabilityChange = vm::onAvailabilityChange,
        onAddressChange = vm::onAddressChange,
        onSinceChange = vm::onSinceChange,
        onSaveChanges = vm::saveChanges,
        onDeleteDoctor = vm::deleteDoctor,
        modifier = modifier
    )
}

@Composable
private fun AdminManageDoctorScreen(
    state: AdminManageDoctorUiState,
    onDoctorIdChange: (String) -> Unit,
    onSearchDoctor: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onContactNumberChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConsultationRateChange: (String) -> Unit,
    onSalaryChange: (String) -> Unit,
    onBonusChange: (String) -> Unit,
    onSpecialtyIdChange: (String) -> Unit,
    onSpecialtyChange: (String) -> Unit,
    onAvailabilityChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onSinceChange: (String) -> Unit,
    onSaveChanges: () -> Unit,
    onDeleteDoctor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Clinica ATL",
            modifier = Modifier.height(90.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Administrar Doctores",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.doctorIdQuery,
                onValueChange = onDoctorIdChange,
                label = { Text("ID del doctor") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            )
            Button(onClick = onSearchDoctor) {
                Text("Buscar")
            }
        }

        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (state.infoMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = state.infoMessage,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Información personal",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = state.firstName,
            onValueChange = onFirstNameChange,
            label = { Text("Nombre") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.lastName,
            onValueChange = onLastNameChange,
            label = { Text("Apellido") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.birthDate,
            onValueChange = onBirthDateChange,
            label = { Text("Fecha de nacimiento") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Correo electrónico") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.contactNumber,
            onValueChange = onContactNumberChange,
            label = { Text("Número de contacto") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña temporal") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Informacion profesional",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = state.specialtyId,
            onValueChange = onSpecialtyIdChange,
            label = { Text("ID especialidad") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.specialty,
            onValueChange = onSpecialtyChange,
            label = { Text("Especialidad") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.availability,
            onValueChange = onAvailabilityChange,
            label = { Text("Disponibilidad") },
            enabled = state.isDoctorLoaded,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.address,
            onValueChange = onAddressChange,
            label = { Text("Dirección consulta") },
            enabled = state.isDoctorLoaded,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.consultationRate,
            onValueChange = onConsultationRateChange,
            label = { Text("Valor consulta") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.salary,
            onValueChange = onSalaryChange,
            label = { Text("Salario") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.bonus,
            onValueChange = onBonusChange,
            label = { Text("Bono") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.since,
            onValueChange = onSinceChange,
            label = { Text("Trabaja desde") },
            enabled = state.isDoctorLoaded,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    onSaveChanges()

                    Toast.makeText(context, "Cambios guardados", Toast.LENGTH_SHORT).show()
                },
                enabled = state.isDoctorLoaded,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF27AE60),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }


            Button(
                onClick = onDeleteDoctor,
                enabled = state.isDoctorLoaded,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Eliminar doctor", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
