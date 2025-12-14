package com.example.app_clinica_atl.ui.screen.admin

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.ui.profile.DEFAULT_AVATAR_URL
import com.example.app_clinica_atl.ui.viewmodel.admin.AdminProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    viewModel: AdminProfileViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val profilePhotoUrl by viewModel.profilePhotoUrl.collectAsStateWithLifecycle()
    val isUploadingPhoto by viewModel.isUploadingPhoto.collectAsStateWithLifecycle()
    val photoErrorMessage by viewModel.photoErrorMessage.collectAsStateWithLifecycle()
    val authToken by viewModel.authToken.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // ===== Validaciones en tiempo real (solo al editar) =====
    val nombreError = remember(uiState.nombre, uiState.isEditing) {
        if (uiState.isEditing) validateNombre(uiState.nombre) else null
    }
    val apellidoError = remember(uiState.apellido, uiState.isEditing) {
        if (uiState.isEditing) validateApellido(uiState.apellido) else null
    }
    val emailError = remember(uiState.email, uiState.isEditing) {
        if (uiState.isEditing) validateGmail(uiState.email) else null
    }
    val telefonoError = remember(uiState.telefono, uiState.isEditing) {
        if (uiState.isEditing) validatePhone(uiState.telefono) else null
    }

    val canSaveProfile = uiState.isEditing &&
            !uiState.isLoading &&
            nombreError == null &&
            apellidoError == null &&
            emailError == null &&
            telefonoError == null

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { viewModel.onNewProfilePhotoSelected(it, context) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onNewProfilePhotoSelected(it, context) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) viewModel.clearMsg()
    }

    var currentPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Datos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = buildAuthImageRequest(
                        context = context,
                        url = profilePhotoUrl ?: DEFAULT_AVATAR_URL,
                        token = authToken
                    ),
                    contentDescription = "Avatar",
                    placeholder = painterResource(id = R.drawable.logo_clean),
                    error = painterResource(id = R.drawable.logo_clean),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                if (isUploadingPhoto) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                TextButton(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Tomar foto")
                }
                TextButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Text("Galería")
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

            // =========================
            // Formulario con validación
            // =========================
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = { raw ->
                    val sanitized = validarNombreaddmin(raw, maxLen = 40)
                    viewModel.onNombreChange(sanitized)
                },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isEditing,
                singleLine = true,
                isError = uiState.isEditing && nombreError != null,
                supportingText = {
                    if (uiState.isEditing && nombreError != null) {
                        Text(nombreError, color = MaterialTheme.colorScheme.error)
                    } else if (uiState.isEditing) {
                        Text("${uiState.nombre.length}/40")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.apellido,
                onValueChange = { raw ->
                    val sanitized = validarNombreaddmin(raw, maxLen = 40)
                    viewModel.onApellidoChange(sanitized)
                },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isEditing,
                singleLine = true,
                isError = uiState.isEditing && apellidoError != null,
                supportingText = {
                    if (uiState.isEditing && apellidoError != null) {
                        Text(apellidoError, color = MaterialTheme.colorScheme.error)
                    } else if (uiState.isEditing) {
                        Text("${uiState.apellido.length}/40")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { raw ->
                    val sanitized = sanitizeEmail(raw, maxLen = 60)
                    viewModel.onEmailChange(sanitized)
                },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isEditing,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = uiState.isEditing && emailError != null,
                supportingText = {
                    if (uiState.isEditing && emailError != null) {
                        Text(emailError, color = MaterialTheme.colorScheme.error)
                    } else if (uiState.isEditing) {
                        Text("${uiState.email.length}/60")
                    }
                },
                placeholder = { Text("usuario@gmail.com") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.telefono,
                onValueChange = { raw ->
                    val sanitized = sanitizePhone(raw)
                    viewModel.onTelefonoChange(sanitized)
                },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isEditing,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                isError = uiState.isEditing && telefonoError != null,
                supportingText = {
                    if (uiState.isEditing && telefonoError != null) {
                        Text(telefonoError, color = MaterialTheme.colorScheme.error)
                    } else if (uiState.isEditing) {
                        Text("${uiState.telefono.length}/12") // +569 + 8 dígitos = 12
                    }
                },
                placeholder = { Text("+56912345678") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = viewModel::toggleChangePassword,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cambiar contraseña")
            }

            val isDialogFieldsEnabled = !uiState.isPasswordUpdating
            val isConfirmEnabled = uiState.currentPassword.isNotBlank() &&
                    uiState.newPassword.isNotBlank() &&
                    uiState.confirmPassword.isNotBlank() &&
                    uiState.newPasswordError == null &&
                    uiState.confirmPasswordError == null &&
                    uiState.newPassword == uiState.confirmPassword &&
                    !uiState.isPasswordUpdating

            if (uiState.isChangingPassword) {
                AlertDialog(
                    onDismissRequest = viewModel::toggleChangePassword,
                    confirmButton = {
                        Button(
                            onClick = viewModel::changePassword,
                            enabled = isConfirmEnabled
                        ) {
                            if (uiState.isPasswordUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("Guardar contraseña")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::toggleChangePassword) {
                            Text("Cancelar")
                        }
                    },
                    title = { Text("Cambiar contraseña") },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = uiState.currentPassword,
                                onValueChange = viewModel::onCurrentPasswordChange,
                                label = { Text("Contraseña actual") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                        val icon = if (currentPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                                        Icon(imageVector = icon, contentDescription = null)
                                    }
                                },
                                isError = uiState.currentPasswordError != null,
                                enabled = isDialogFieldsEnabled
                            )
                            uiState.currentPasswordError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }

                            OutlinedTextField(
                                value = uiState.newPassword,
                                onValueChange = viewModel::onNewPasswordChange,
                                label = { Text("Nueva contraseña") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                        val icon = if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                                        Icon(imageVector = icon, contentDescription = null)
                                    }
                                },
                                isError = uiState.newPasswordError != null,
                                enabled = isDialogFieldsEnabled
                            )
                            uiState.newPasswordError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }

                            OutlinedTextField(
                                value = uiState.confirmPassword,
                                onValueChange = viewModel::onConfirmPasswordChange,
                                label = { Text("Confirmar nueva contraseña") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                                        Icon(imageVector = icon, contentDescription = null)
                                    }
                                },
                                isError = uiState.confirmPasswordError != null,
                                enabled = isDialogFieldsEnabled
                            )
                            uiState.confirmPasswordError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }

                            uiState.passwordChangeError?.let {
                                Text(
                                    it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                )
            }

            // Mensajes y Botones
            if (uiState.errorMsg != null) {
                Text(uiState.errorMsg!!, color = MaterialTheme.colorScheme.error)
            }
            if (uiState.updateSuccess) {
                Text("¡Actualizado correctamente!", color = Color.Green)
            }
            if (uiState.passwordChangeSuccess) {
                Text(
                    "Contraseña actualizada correctamente.",
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!uiState.isEditing) {
                Button(onClick = viewModel::toggleEdit, modifier = Modifier.fillMaxWidth()) {
                    Text("Editar Perfil")
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = viewModel::toggleEdit, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = viewModel::updateProfile,
                        enabled = canSaveProfile,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("Guardar")
                    }
                }
            }
        }
    }
}

private fun buildAuthImageRequest(context: Context, url: String?, token: String?): ImageRequest {
    val builder = ImageRequest.Builder(context)
        .data(url ?: DEFAULT_AVATAR_URL)
        .crossfade(true)
    if (!token.isNullOrBlank() && !url.isNullOrBlank()) {
        builder.addHeader("Authorization", "Bearer $token")
    }
    return builder.build()
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

// ==========================
// Helpers: sanitizar/validar
// ==========================
private val GMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@atladmin\\.cl$", RegexOption.IGNORE_CASE)
private val PHONE_REGEX = Regex("^\\+569\\d{8}$")

private fun validarNombreaddmin(input: String, maxLen: Int): String {
    return input
        .filter { it.isLetter() || it == ' ' }
        .take(maxLen)
}

private fun sanitizeEmail(input: String, maxLen: Int): String {
    return input
        .trim()
        .replace(" ", "")
        .take(maxLen)
}

private fun sanitizePhone(input: String): String {
    // Permite solo números y fuerza siempre +569 + 8 dígitos
    val digits = input.filter { it.isDigit() }

    val afterPrefix = when {
        digits.startsWith("569") -> digits.drop(3)
        digits.startsWith("56") -> digits.drop(2)
        digits.startsWith("9") -> digits.drop(1)
        else -> digits
    }.take(8)

    return "+569$afterPrefix"
}

private fun validateNombre(value: String): String? {
    if (value.isBlank()) return "El nombre no puede quedar vacío"
    if (value.length > 40) return "Máximo 40 caracteres"
    if (value.any { it.isDigit() }) return "No se permiten números"
    if (value.any { !(it.isLetter() || it == ' ') }) return "Solo letras"
    return null
}

private fun validateApellido(value: String): String? {
    if (value.isBlank()) return "El apellido no puede quedar vacío"
    if (value.length > 40) return "Máximo 40 caracteres"
    if (value.any { it.isDigit() }) return "No se permiten números"
    if (value.any { !(it.isLetter() || it == ' ') }) return "Solo letras"
    return null
}

private fun validateGmail(value: String): String? {
    if (value.isBlank()) return "El correo no puede quedar vacío"
    if (value.length > 60) return "Máximo 60 caracteres"
    if (!GMAIL_REGEX.matches(value)) return "Debe ser un correo @atladmin.cl"
    return null
}

private fun validatePhone(value: String): String? {
    if (!value.startsWith("+569")) return "Debe comenzar con +569"
    val tail = value.removePrefix("+569")

    if (tail.isEmpty()) return "Ingresa 8 dígitos después de +569"
    if (tail.length < 8) return "Faltan ${8 - tail.length} dígitos"
    if (tail.length > 8) return "Sobran dígitos"
    if (!tail.all { it.isDigit() }) return "Solo números"
    if (!PHONE_REGEX.matches(value)) return "Formato: +569XXXXXXXX"

    return null
}
