package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.ui.viewmodel.InsuranceViewModel
import kotlinx.coroutines.delay


@Composable
fun SegurosScreen(
    viewModel: InsuranceViewModel
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

        // ---- SALUD ----
        Text(
            text = "Seguros de Salud",
            style = MaterialTheme.typography.titleLarge
        )

        if (state.healthInsurances.isEmpty()) {
            Text("No hay seguros de salud disponibles.")
        } else {
            state.healthInsurances.forEach { seguro ->
                SeguroItem(
                    seguro = seguro,
                    onClick = { viewModel.subscribeToInsurance(seguro.id) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ---- VIDA ----
        Text(
            text = "Seguros de Vida",
            style = MaterialTheme.typography.titleLarge
        )

        if (state.lifeInsurances.isEmpty()) {
            Text("No hay seguros de vida disponibles.")
        } else {
            state.lifeInsurances.forEach { seguro ->
                SeguroItem(
                    seguro = seguro,
                    onClick = { viewModel.subscribeToInsurance(seguro.id) }
                )
            }
        }

        // ---- MENSAJES ----
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
    TODO()
}

