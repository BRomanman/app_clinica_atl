package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.local.insurance.InsuranceEntity
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel

/**
 * Pantalla de Perfil de Paciente
 * ¡SIN TopBar y SIN 'onBackClick'!
 */
@Composable
fun PatientProfileScreen(
    onGoToSeguros: () -> Unit,
    viewModel: PatientViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMsg, uiState.successMsg) {
        uiState.errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // Usamos un Scaffold solo por el Snackbar, pero sin TopBar/BottomBar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Padding del Scaffold
                .padding(16.dp),      // Padding de la pantalla
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading && uiState.patient == null -> {
                    CircularProgressIndicator()
                }
                uiState.errorMsg != null && uiState.patient == null -> {
                    Text(
                        text = uiState.errorMsg ?: "No se pudo cargar el perfil.",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
                uiState.patient != null -> {
                    PatientProfileContent(
                        patient = uiState.patient!!,
                        activeInsurance = uiState.activeInsuranceDetails,
                        onCancelInsurance = viewModel::cancelSubscription,
                        onGoToSeguros = onGoToSeguros
                    )
                }
            }
        }
    }
}

@Composable
private fun PatientProfileContent(
    patient: UserEntity,
    activeInsurance: InsuranceEntity?,
    onCancelInsurance: () -> Unit,
    onGoToSeguros: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.goku_perfil),
            contentDescription = "Foto de ${patient.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = patient.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = patient.role.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        InfoRow(label = "Email", value = patient.email)
        InfoRow(label = "Teléfono", value = patient.phone)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        InsuranceInfoCard(
            activeInsurance = activeInsurance,
            onCancelInsurance = onCancelInsurance,
            onGoToSeguros = onGoToSeguros
        )
    }
}

@Composable
private fun InsuranceInfoCard(
    activeInsurance: InsuranceEntity?,
    onCancelInsurance: () -> Unit,
    onGoToSeguros: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Mi Seguro Médico",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (activeInsurance != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(activeInsurance.name, fontWeight = FontWeight.SemiBold)
                        Text(
                            "\$${activeInsurance.price.toInt()} / mensual",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = onCancelInsurance,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancelar")
                    }
                }
            } else {
                Text("No tienes ningún seguro activo.")
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onGoToSeguros) {
                    Text("Ver planes disponibles")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}