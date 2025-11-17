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
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileScreen(
    doctorId: Long?,
    onBackClick: () -> Unit,
    viewModel: DoctorProfileViewModel,
    modifier: Modifier = Modifier // <-- ¡¡PARÁMETRO AÑADIDO!!
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(doctorId) {
        if (doctorId != null) {
            viewModel.loadDoctorProfile(doctorId)
        }
    }

    Scaffold(
        // ¡¡MODIFIER APLICADO!!
        // Esto permite que el NavGraph le quite el padding
        modifier = modifier.fillMaxSize(),
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
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.errorMsg != null || uiState.doctor == null -> {
                    Text(
                        text = uiState.errorMsg ?: "No se pudo encontrar al doctor.",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    DoctorProfileContent(doctor = uiState.doctor!!)
                }
            }
        }
    }
}

@Composable
private fun DoctorProfileContent(doctor: UsuarioEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = getDoctorImageResource(doctor.specialty)),
            contentDescription = "Foto de ${doctor.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = doctor.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = doctor.specialty ?: "Especialidad no definida",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        InfoRow(label = "Email", value = doctor.email)
        InfoRow(label = "Teléfono", value = doctor.phone)
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