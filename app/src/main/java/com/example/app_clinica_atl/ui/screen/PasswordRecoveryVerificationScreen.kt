package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel

@Composable
fun PasswordRecoveryVerificationScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    val uiState by viewModel.loginUiState.collectAsState()

    LaunchedEffect(uiState.recoveryIdentityPassed) {
        if (uiState.recoveryIdentityPassed) {
            onVerified()
            viewModel.consumeRecoveryIdentityFlag()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.size(48.dp))
        Text(
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Correo registrado",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = uiState.recoveryEmail.ifBlank { uiState.email },
                onValueChange = viewModel::onRecoveryEmailChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.recoveryEmailError != null,
                label = { Text("Correo electrónico") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            uiState.recoveryEmailError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Fecha de nacimiento",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            val birthFieldValue = remember(uiState.recoveryBirthDate) {
                val text = uiState.recoveryBirthDate
                TextFieldValue(text = text, selection = TextRange(text.length))
            }
            OutlinedTextField(
                value = birthFieldValue,
                onValueChange = { viewModel.onRecoveryBirthDateChange(it.text) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.recoveryBirthDateError != null,
                label = { Text("DD-MM-YYYY") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            uiState.recoveryBirthDateError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
        }

        uiState.recoveryVerificationError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            onClick = viewModel::verifyRecoveryIdentity,
            enabled = !uiState.isVerifyingRecovery,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isVerifyingRecovery) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.size(8.dp))
            }
            Text(if (uiState.isVerifyingRecovery) "Verificando..." else "Continuar")
        }
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Volver")
        }
    }
}
