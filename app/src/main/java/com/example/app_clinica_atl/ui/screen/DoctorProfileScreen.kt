package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// NO MÁS HILT
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileScreen(
    doctorId: Long?,
    onBackClick: () -> Unit,
    viewModel: DoctorProfileViewModel // <-- Recibe el VM como parámetro
) {
    val uiState by viewModel.uiState.collectAsState()

    // Efecto de carga:
    // Cuando la pantalla recibe un ID, le dice al ViewModel que cargue
    // el perfil de ESE doctor.
    LaunchedEffect(doctorId) {
        if (doctorId != null) {
            viewModel.loadDoctorProfile(doctorId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.doctor?.name ?: "Perfil de Doctor") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                // 1. Estado de Carga
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                // 2. Estado de Error
                uiState.errorMsg != null || uiState.doctor == null -> {
                    Text(
                        text = uiState.errorMsg ?: "No se pudo encontrar al doctor.",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                // 3. Estado con Datos (Éxito)
                else -> {
                    // Mostramos el perfil del doctor
                    DoctorProfileContent(doctor = uiState.doctor!!)
                }
            }
        }
    }
}

/**
 * Composable que muestra el contenido del perfil del doctor.
 */
@Composable
private fun DoctorProfileContent(doctor: UserEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen del Doctor
        Image(
            painter = painterResource(id = getDoctorImageResource(doctor.specialty)),
            contentDescription = "Foto de ${doctor.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre
        Text(
            text = doctor.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Especialidad
        Text(
            text = doctor.specialty ?: "Especialidad no definida",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Información de Contacto
        InfoRow(label = "Email", value = doctor.email)
        InfoRow(label = "Teléfono", value = doctor.phone)

        // (Podríamos añadir más info aquí si la tuviéramos)
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

/**
 * Función helper (la misma de la pantalla de búsqueda)
 */
@Composable
private fun getDoctorImageResource(specialty: String?): Int {
    return when (specialty) {
        "Cardiología" -> R.drawable.doctor_cardio_1
        "Dermatología" -> R.drawable.doctor_derma_1
        "Medicina General" -> R.drawable.doctor_medgen_1
        "Pediatría" -> R.drawable.doctor_pedi_1
        "Psicología" -> R.drawable.doctor_psico_1
        else -> R.drawable.logo_clean
    }
}