package com.example.app_clinica_atl.ui.screen.patient

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.ui.profile.DEFAULT_AVATAR_URL
import com.example.app_clinica_atl.data.remote.dto.CitaDetalleDto
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.ui.viewmodel.patient.PatientViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Composable
fun PatientProfileScreen(
    onGoToSeguros: () -> Unit,
    onGoToChangePassword: () -> Unit,
    onLogout: () -> Unit,
    viewModel: PatientViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val profilePhotoUrl by viewModel.profilePhotoUrl.collectAsStateWithLifecycle()
    val isUploadingPhoto by viewModel.isUploadingPhoto.collectAsStateWithLifecycle()
    val photoErrorMessage by viewModel.photoErrorMessage.collectAsStateWithLifecycle()

    // --- Lógica de Cámara (Launchers) ---
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let {
                viewModel.onNewProfilePhotoSelected(it, context)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onNewProfilePhotoSelected(it, context) }
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


    LaunchedEffect(uiState.errorMsg) {
        uiState.errorMsg?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.TopCenter
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
                        insurances = uiState.insurances,
                        appointments = uiState.activeAppointments,
                        firstNameInput = uiState.firstNameInput,
                        lastNameInput = uiState.lastNameInput,
                        emailInput = uiState.emailInput,
                        firstNameError = uiState.firstNameError,
                        lastNameError = uiState.lastNameError,
                        emailError = uiState.emailError,
                        isSavingPersonalData = uiState.isSavingPersonalData,
                        phoneInput = uiState.phoneInput,
                        phoneError = uiState.phoneError,
                        isSavingPhone = uiState.isSavingPhone,
                        onCancelInsurance = viewModel::cancelSubscription,
                        onCancelAppointment = viewModel::cancelAppointment,
                        onGoToSeguros = onGoToSeguros,
                        onGoToChangePassword = onGoToChangePassword,
                        onLogout = onLogout,
                        onFirstNameChange = viewModel::onFirstNameChange,
                        onLastNameChange = viewModel::onLastNameChange,
                        onEmailChange = viewModel::onEmailChange,
                        onSavePersonalData = viewModel::savePersonalData,
                        onPhoneChange = viewModel::onPhoneChange,
                        onSavePhone = viewModel::savePhone,
                        profilePhotoUrl = profilePhotoUrl,
                        isUploadingPhoto = isUploadingPhoto,
                        photoErrorMessage = photoErrorMessage,
                        onRequestCamera = {
                            // Pide permiso de cámara al hacer clic
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        onRequestGallery = {
                            galleryLauncher.launch("image/*")
                        }
                    )
                }
            }
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val imageCacheFolder = File(context.cacheDir, "images")
    if (!imageCacheFolder.exists()) {
        imageCacheFolder.mkdirs()
    }

    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile = File(imageCacheFolder, "JPEG_${timeStamp}_.jpg")

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
    insurances: List<SeguroDto>,
    appointments: List<CitaDetalleDto>,
    firstNameInput: String,
    lastNameInput: String,
    emailInput: String,
    firstNameError: String?,
    lastNameError: String?,
    emailError: String?,
    isSavingPersonalData: Boolean,
    phoneInput: String,
    phoneError: String?,
    isSavingPhone: Boolean,
    onCancelInsurance: () -> Unit,
    onCancelAppointment: (Long) -> Unit,
    onGoToSeguros: () -> Unit,
    onGoToChangePassword: () -> Unit,
    onLogout: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onSavePersonalData: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onSavePhone: () -> Unit,
    profilePhotoUrl: String?,
    isUploadingPhoto: Boolean,
    photoErrorMessage: String?,
    onRequestCamera: () -> Unit,
    onRequestGallery: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Item 1: Imagen de Perfil ---
        item {

            Box(
                modifier = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = profilePhotoUrl ?: DEFAULT_AVATAR_URL,
                    contentDescription = "Foto de ${patient.name}",
                    placeholder = painterResource(id = R.drawable.logo_clean),
                    error = painterResource(id = R.drawable.logo_clean),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .clickable { onRequestCamera() }
                )
                if (isUploadingPhoto) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 3.dp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onRequestCamera) {
                    Text("Tomar foto")
                }
                TextButton(onClick = onRequestGallery) {
                    Text("Elegir foto desde archivos")
                }
            }
            photoErrorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
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
            Text(
                text = patient.email,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileEditorCard(
                firstName = firstNameInput,
                lastName = lastNameInput,
                email = emailInput,
                phoneInput = phoneInput,
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                emailError = emailError,
                phoneError = phoneError,
                isSavingPersonalData = isSavingPersonalData,
                isSavingPhone = isSavingPhone,
                onGoToChangePassword = onGoToChangePassword,
                onFirstNameChange = onFirstNameChange,
                onLastNameChange = onLastNameChange,
                onEmailChange = onEmailChange,
                onPhoneChange = onPhoneChange,
                onSavePersonalData = onSavePersonalData,
                onSavePhone = onSavePhone
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Item 2: El Seguro (sin cambios) ---
        item {
            InsuranceInfoCard(
                activeInsurance = activeInsurance,
                insurances = insurances,
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
                    onCancel = { appointment.appointmentId?.let { onCancelAppointment(it) } }
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
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
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
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
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
    insurances: List<SeguroDto>,
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
                text = "Mis Seguros Médicos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (activeInsurance != null) {
                val orderedInsurances = insurances
                    .sortedBy { it.name }
                orderedInsurances.forEach { seguro ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(seguro.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                "\$${seguro.price.toInt()} / mensual",
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
                }
            }
            else {
                Text("No tienes ningún seguro activo.")
            }
        }
    }
}

@Composable
private fun ProfileEditorCard(
    firstName: String,
    lastName: String,
    email: String,
    phoneInput: String,
    firstNameError: String?,
    lastNameError: String?,
    emailError: String?,
    phoneError: String?,
    isSavingPersonalData: Boolean,
    isSavingPhone: Boolean,
    onGoToChangePassword: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSavePersonalData: () -> Unit,
    onSavePhone: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row {
                    Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                    Text(
                        text = "Datos personales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column {
                            FieldTitle(icon = Icons.Filled.Person, text = "Nombre")
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = onFirstNameChange,
                                modifier = Modifier.fillMaxWidth(),
                                isError = firstNameError != null,
                                singleLine = true
                            )
                            firstNameError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Column {
                            FieldTitle(icon = Icons.Filled.Person, text = "Apellido")
                            OutlinedTextField(
                                value = lastName,
                                onValueChange = onLastNameChange,
                                modifier = Modifier.fillMaxWidth(),
                                isError = lastNameError != null,
                                singleLine = true
                            )
                            lastNameError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    Column {
                        FieldTitle(icon = Icons.Filled.Email, text = "Correo electrónico")
                        OutlinedTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            modifier = Modifier.fillMaxWidth(),
                            isError = emailError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        emailError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Column {
                        FieldTitle(icon = Icons.Filled.Phone, text = "Teléfono (+569...)")
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = onPhoneChange,
                            modifier = Modifier.fillMaxWidth(),
                            isError = phoneError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        phoneError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    val isSavingPersonalOrPhone = isSavingPersonalData || isSavingPhone
                    Button(
                        onClick = {
                            onSavePersonalData()
                            onSavePhone()
                        },
                        enabled = !isSavingPersonalOrPhone,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSavingPersonalOrPhone) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (isSavingPersonalOrPhone) "Guardando..." else "Guardar datos")
                    }
                }
            }


            //cambio de contraseña
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Seguridad",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Button(
                    onClick = onGoToChangePassword,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Cambiar contraseña")
                }
            }
        }
    }
}

@Composable
private fun FieldTitle(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
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
