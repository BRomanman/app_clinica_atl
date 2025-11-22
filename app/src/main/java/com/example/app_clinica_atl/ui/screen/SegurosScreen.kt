package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.ui.viewmodel.InsuranceViewModel
import kotlinx.coroutines.delay

@Composable
fun SegurosScreen(
    viewModel: InsuranceViewModel,
    onSeguroSeleccionado: (SeguroDto) -> Unit
) {
    val state = viewModel.uiState.collectAsState().value

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Seguros de Salud", style = MaterialTheme.typography.titleLarge)

        if (state.healthInsurances.isEmpty()) {
            Text("No hay seguros de salud disponibles.")
        } else {
            state.healthInsurances.forEach { seguro ->
                SeguroItem(
                    seguro = seguro,
                    onClick = { onSeguroSeleccionado(seguro) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Seguros de Vida", style = MaterialTheme.typography.titleLarge)

        if (state.lifeInsurances.isEmpty()) {
            Text("No hay seguros de vida disponibles.")
        } else {
            state.lifeInsurances.forEach { seguro ->
                SeguroItem(
                    seguro = seguro,
                    onClick = { onSeguroSeleccionado(seguro) }
                )
            }
        }

        // MENSAJES
        state.errorMsg?.let { msg ->
            Spacer(Modifier.height(16.dp))
            Text(msg, color = Color.Red)
            LaunchedEffect(msg) {
                delay(2500)
                viewModel.clearMessages()
            }
        }

        state.successMsg?.let { msg ->
            Spacer(Modifier.height(16.dp))
            Text(msg, color = Color(0xFF0A7F0A))
            LaunchedEffect(msg) {
                delay(2500)
                viewModel.clearMessages()
            }
        }
    }
}

@Composable
fun SeguroItem(seguro: SeguroDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(seguro.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(seguro.description)
            Spacer(Modifier.height(6.dp))
            Text("Precio: $${seguro.price}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
