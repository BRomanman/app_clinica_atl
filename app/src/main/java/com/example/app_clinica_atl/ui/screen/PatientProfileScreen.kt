package com.example.app_clinica_atl.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.remote.dto.CitaDetalleDto
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Composable
fun PatientProfileScreen(
    onGoToSeguros: () -> Unit,
    onLogout: () -> Unit,
    viewModel: PatientViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // --- Lógica de Cámara (Launchers) ---
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let {
                viewModel.updateProfileImage(it)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permiso concedido, crea la URI y lanza la cámara
            val uri = createImageUri(context) // Llama a la función corregida
            tempImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            // Permiso denegado, (podríamos mostrar un snackbar)
            viewModel.clearMessages()
        }
    }
    // --- Fin Lógica de Cámara ---


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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                // (El estado isLoading ahora viene del flow reactivo)
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.errorMsg != null && uiState.patient == null -> {
                    // Muestra error si el perfil no se pudo cargar
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
                        appointments = uiState.activeAppointments,
                        onCancelInsurance = viewModel::cancelSubscription,
                        onCancelAppointment = viewModel::cancelAppointment,
                        onGoToSeguros = onGoToSeguros,
                        onLogout = onLogout,
                        onProfileImageClick = {
                            // Pide permiso de cámara al hacer clic
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )
                }
            }
        }
    }
}

/**
 * ¡¡FUNCIÓN CORREGIDA!!
 * Ahora usa 'context.cacheDir' y la subcarpeta 'images'
 * para que coincida con el '<cache-path ... path="images/" />'
 * de tu 'file_paths.xml'.
 */
private fun createImageUri(context: Context): Uri {
    // 1. Define la subcarpeta "images" (debe coincidir con path="images/" en file_paths.xml)
    val imageCacheFolder = File(context.cacheDir, "images")
    if (!imageCacheFolder.exists()) {
        imageCacheFolder.mkdirs() // Crea la carpeta si no existe
    }

    // 2. Crea el archivo temporal DENTRO de esa subcarpeta
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile = File(imageCacheFolder, "JPEG_${timeStamp}_.jpg")

    // 3. Construye la autoridad (debe coincidir con .fileprovider en AndroidManifest.xml)
    val authority = "${context.packageName}.fileprovider"

    return FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        authority,
        imageFile
    )
}

@Composable
private fun PatientProfileContent(
    patient: UsuarioDto,
    activeInsurance: SeguroDto?,
    appointments: List<CitaDetalleDto>,
    onCancelInsurance: () -> Unit,
    onCancelAppointment: (Long) -> Unit,
    onGoToSeguros: () -> Unit,
    onLogout: () -> Unit,
    onProfileImageClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Item 1: Imagen de Perfil ---
        item {
            AsyncImage(
                model = patient.profileImageUrl, // Carga la URL de la BD (String o Uri)
                contentDescription = "Foto de ${patient.name}",
                placeholder = painterResource(id = R.drawable.goku_perfil), // Tu placeholder
                error = painterResource(id = R.drawable.goku_perfil), // Tu placeholder si falla
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .clickable { onProfileImageClick() } // <-- ¡Acción de cámara!
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
        }

        // --- Item 2: El Seguro (sin cambios) ---
        item {
            InsuranceInfoCard(
                activeInsurance = activeInsurance,
                onCancelInsurance = onCancelInsurance,
                onGoToSeguros = onGoToSeguros
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Item 3: Título de Citas (sin cambios) ---
        item {
            Text(
                text = "Mis Próximas Citas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Item 4: Lista de Citas (sin cambios) ---
        if (appointments.isEmpty()) {
            item {
                Text(
                    "No tienes citas agendadas.",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        } else {
            items(appointments) { appointment ->
                AppointmentCard(
                    appointment = appointment,
                    onCancel = { onCancelAppointment(appointment.appointmentId) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // --- Item 5: Botón de Logout (sin cambios) ---
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

// ... (AppointmentCard, InsuranceInfoCard, InfoRow no cambian) ...
@Composable
private fun AppointmentCard(
    appointment: CitaDetalleDto,
    onCancel: () -> Unit
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
                text = "Dr. ${appointment.doctorName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = appointment.doctorSpecialty,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(label = "Fecha", value = appointment.date)
            InfoRow(label = "Hora", value = appointment.time)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar Cita")
            }
        }
    }
}

@Composable
private fun InsuranceInfoCard(
    activeInsurance: SeguroDto?,
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
            .padding(vertical = 4.dp)
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
