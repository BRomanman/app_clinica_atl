package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
<<<<<<< Updated upstream
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.ui.viewmodel.InsuranceViewModel
=======
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.ui.viewmodel.InsuranceViewModel
import kotlinx.coroutines.delay
import kotlin.collections.random
>>>>>>> Stashed changes

/**
 * Pantalla de Seguros
 * ¡SIN TopBar y SIN 'onBackClick'!
 */
@Composable
fun SegurosScreen(
    viewModel: InsuranceViewModel
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

<<<<<<< Updated upstream
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.availableInsurances) { insurance ->
                        InsuranceCard(
                            insurance = insurance,
                            onSubscribe = { viewModel.subscribeToInsurance(insurance.id) }
                        )
                    }
                }
=======
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Nuestros Seguros",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            "Seguros de Salud",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (state.healthInsurances.isEmpty()) {
            Text("No hay seguros de salud disponibles.")
        } else {
            state.healthInsurances.forEach { seguro ->
                SeguroCard(
                    seguro = seguro,
                    onClick = { onSeguroSeleccionado(seguro) }
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            "Seguros de Vida",
            style = MaterialTheme.typography.titleLarge
        )

        if (state.lifeInsurances.isEmpty()) {
            Text("No hay seguros de vida disponibles.")
        } else {
            state.lifeInsurances.forEach { seguro ->
                SeguroCard(
                    seguro = seguro,
                    onClick = { onSeguroSeleccionado(seguro) }
                )
            }
        }

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
>>>>>>> Stashed changes
            }
        }
    }
}
val seguroImages = listOf(
    R.drawable.seguro_1,
    R.drawable.seguro_2,
    R.drawable.seguro_3,
    R.drawable.seguro_empresarial1,
    R.drawable.seguro_empresarial2,
    R.drawable.seguro_empresarial3,
    R.drawable.seguro_salud_1,
    R.drawable.seguro_salud_2,
    R.drawable.seguro_salud_3,
    R.drawable.seguro_vida_1,
    R.drawable.seguro_vida_2,
    R.drawable.seguro_vida_3
)
@Composable
<<<<<<< Updated upstream
private fun InsuranceCard(
    insurance: SeguroDto,
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = getInsuranceImage(insurance.id)),
                contentDescription = insurance.name,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = insurance.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = insurance.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "\$${insurance.price.toInt()} / mensual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSubscribe,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Contratar Ahora")
=======
fun SeguroCard(seguro: SeguroDto, onClick: () -> Unit) {

    val randomImage = remember { seguroImages.random() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth()
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = randomImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Degradado oscuro sobre la imagen
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.1f),
                                Color.Black.copy(alpha = 0.65f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    seguro.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    seguro.description,
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Precio: $${seguro.price}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
>>>>>>> Stashed changes
            }
        }
    }
}

<<<<<<< Updated upstream
@Composable
private fun getInsuranceImage(insuranceId: Long): Int {
    return when (insuranceId) {
        1L -> R.drawable.seguro_salud_1
        2L -> R.drawable.familia_feliz1
        3L -> R.drawable.seguro_vida_1
        else -> R.drawable.logo_clean
    }
}
=======
>>>>>>> Stashed changes
