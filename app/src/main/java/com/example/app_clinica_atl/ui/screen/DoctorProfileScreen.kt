package com.example.app_clinica_atl.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.remote.dto.DoctorMonthlyStatDto
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileInfo
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileUiState
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.Objects

@RequiresApi(Build.VERSION_CODES.O)
private val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale("es", "CL"))

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorProfileScreen(
    doctorId: Long?,
    onBackClick: () -> Unit,
    viewModel: DoctorProfileViewModel,
    modifier: Modifier = Modifier,
    isPublicView: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { viewModel.updateProfileImage(it) }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            tempImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(doctorId) {
        if (doctorId != null) {
            viewModel.loadDoctorProfile(doctorId)
        }
    }

    LaunchedEffect(uiState.transientError, uiState.successMsg) {
        uiState.transientError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { DoctorProfileTopBar(onBackClick, isPublicView) }
    ) { paddingValues ->
        DoctorProfileContentHost(
            doctorId = doctorId,
            uiState = uiState,
            paddingValues = paddingValues,
            onRequestCamera = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            onPhoneChange = viewModel::onPhoneChange,
            onSavePhone = viewModel::savePhone,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onSavePassword = viewModel::savePassword,
            isPublicView = isPublicView
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DoctorProfileTopBar(onBackClick: () -> Unit, isPublicView: Boolean) {
    TopAppBar(
        title = { Text(if (isPublicView) "Doctor seleccionado" else "Perfil de doctor") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
        }
    )
}






















@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DoctorProfileContentHost(
    doctorId: Long?,
    uiState: DoctorProfileUiState,
    paddingValues: PaddingValues,
    onRequestCamera: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onSavePhone: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSavePassword: () -> Unit,
    isPublicView: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            doctorId == null -> {
                Text(
                    text = "No se pudo abrir el perfil del doctor.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.errorMsg != null && uiState.doctor == null -> {
                Text(
                    text = uiState.errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            uiState.doctor != null -> {
                DoctorProfileContent(
                    doctor = uiState.doctor,
                    phoneInput = uiState.phoneInput,
                    phoneError = uiState.phoneError,
                    isSavingPhone = uiState.isSavingPhone,
                    passwordInput = uiState.passwordInput,
                    confirmPasswordInput = uiState.confirmPasswordInput,
                    passwordError = uiState.passwordError,
                    confirmPasswordError = uiState.confirmPasswordError,
                    isSavingPassword = uiState.isSavingPassword,
                    stats = uiState.stats,
                    totalAppointments = uiState.totalAppointments,
                    bonusAmount = uiState.bonusAmount,
                    isUploadingPhoto = uiState.isUploadingPhoto,
                    onPhoneChange = onPhoneChange,
                    onSavePhone = onSavePhone,
                    onPasswordChange = onPasswordChange,
                    onConfirmPasswordChange = onConfirmPasswordChange,
                    onSavePassword = onSavePassword,
                    onProfileImageClick = onRequestCamera,
                    isPublicView = isPublicView
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DoctorProfileContent(
    doctor: DoctorProfileInfo,
    phoneInput: String,
    phoneError: String?,
    isSavingPhone: Boolean,
    passwordInput: String,
    confirmPasswordInput: String,
    passwordError: String?,
    confirmPasswordError: String?,
    isSavingPassword: Boolean,
    stats: List<DoctorMonthlyStatDto>,
    totalAppointments: Int,
    bonusAmount: Double,
    isUploadingPhoto: Boolean,
    onPhoneChange: (String) -> Unit,
    onSavePhone: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSavePassword: () -> Unit,
    onProfileImageClick: () -> Unit,
    isPublicView: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            DoctorProfileHeader(
                doctor = doctor,
                isUploadingPhoto = isUploadingPhoto,
                onProfileImageClick = onProfileImageClick,
                isPublicView = isPublicView
            )
        }
        item {
            InfoCard(label = "Nombre", value = doctor.name)
        }
        item {
            InfoCard(label = "Correo", value = doctor.email)
        }
        item {
            InfoCard(label = "Especialidad", value = doctor.specialty ?: "Sin especialidad")
        }
        if (isPublicView) {
            item {
                InfoCard(label = "Teléfono", value = doctor.phone.ifBlank { "No disponible" })
            }
            doctor.tarifaConsulta?.let { fee ->
                item {
                    InfoCard(label = "Tarifa de consulta", value = "$$fee")
                }
            }
        } else {
            item {
                PhoneEditorCard(
                    phoneInput = phoneInput,
                    phoneError = phoneError,
                    isSaving = isSavingPhone,
                    onPhoneChange = onPhoneChange,
                    onSavePhone = onSavePhone
                )
            }
            item {
                PasswordEditorCard(
                    passwordInput = passwordInput,
                    confirmPasswordInput = confirmPasswordInput,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    isSaving = isSavingPassword,
                    onPasswordChange = onPasswordChange,
                    onConfirmPasswordChange = onConfirmPasswordChange,
                    onSavePassword = onSavePassword
                )
            }
            item {
                DoctorStatsCard(
                    stats = stats,
                    totalAppointments = totalAppointments,
                    bonusAmount = bonusAmount
                )
            }
        }
    }
}

@Composable
private fun DoctorProfileHeader(
    doctor: DoctorProfileInfo,
    isUploadingPhoto: Boolean,
    onProfileImageClick: () -> Unit,
    isPublicView: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = doctor.profileImageUrl ?: R.drawable.logo_clean,
                    placeholder = painterResource(id = R.drawable.logo_clean),
                    contentDescription = "Foto de ${doctor.name}",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .let { mod -> if (isPublicView) mod else mod.clickable(onClick = onProfileImageClick) },
                    contentScale = ContentScale.Crop
                )
                if (isUploadingPhoto) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = doctor.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            doctor.specialty?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            if (!isPublicView) {
                TextButton(onClick = onProfileImageClick) {
                    Text("Cambiar foto", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PhoneEditorCard(
    phoneInput: String,
    phoneError: String?,
    isSaving: Boolean,
    onPhoneChange: (String) -> Unit,
    onSavePhone: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Datos de contacto", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = phoneInput,
                onValueChange = { raw ->
                    val sanitized = raw.filterIndexed { index, c ->
                        c.isDigit() || (c == '+' && index == 0)
                    }
                    onPhoneChange(sanitized)
                },
                label = { Text("Teléfono") },
                isError = phoneError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                supportingText = { phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSavePhone,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Guardar teléfono")
                }
            }
        }
    }
}

@Composable
private fun PasswordEditorCard(
    passwordInput: String,
    confirmPasswordInput: String,
    passwordError: String?,
    confirmPasswordError: String?,
    isSaving: Boolean,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSavePassword: () -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmVisible by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Seguridad", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = passwordInput,
                onValueChange = onPasswordChange,
                label = { Text("Nueva contraseña") },
                isError = passwordError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar u ocultar contraseña"
                        )
                    }
                },
                supportingText = { passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPasswordInput,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Confirmar contraseña") },
                isError = confirmPasswordError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(
                            imageVector = if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar u ocultar contraseña"
                        )
                    }
                },
                supportingText = { confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSavePassword,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Actualizar contraseña")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DoctorStatsCard(
    stats: List<DoctorMonthlyStatDto>,
    totalAppointments: Int,
    bonusAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Estadísticas mensuales", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Citas (mes más reciente): $totalAppointments",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Bono estimado (10%): ${"%.1f".format(Locale.getDefault(), bonusAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (stats.isEmpty()) {
                Text(
                    text = "Aún no hay citas registradas en historial.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    stats.sortedByDescending { it.month }.forEach { stat ->
                        StatRow(
                            label = formatMonthLabel(stat.month),
                            value = "${stat.totalAppointments} citas"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatMonthLabel(rawMonth: String): String {
    return try {
        YearMonth.parse(rawMonth).format(monthFormatter).replaceFirstChar { it.titlecase(Locale("es", "CL")) }
    } catch (e: Exception) {
        rawMonth
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
    return androidx.core.content.FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        authority,
        imageFile
    )
}
