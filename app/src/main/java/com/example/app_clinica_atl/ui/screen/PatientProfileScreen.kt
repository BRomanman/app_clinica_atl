package com.example.app_clinica_atl.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
// import androidx.compose.material.icons.filled.Upload // <-- ELIMINADO
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
// import androidx.compose.material3.TextFieldDefaults // <-- ELIMINADO
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
// import androidx.compose.ui.text.style.TextAlign // <-- ELIMINADO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validateNamePart
import com.example.app_clinica_atl.domain.validation.validatePhoneDigitsOnly
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Funciones de Utilidad (Uris y Permisos) ---
// (Estas funciones se quedan igual)
private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply {
        if (!exists()) mkdirs()
    }
    return File(storageDir, "IMG_$timeStamp.jpg")
}
private fun getImageUriFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}


// --- COMPOSABLE "INTELIGENTE" (VM) ---
@Composable
fun PatientProfileScreenVm(
    vm: AuthViewModel,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val userData by vm.currentUserData.collectAsStateWithLifecycle()

    if (userData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // --- CAMBIO 1: PASAR NOMBRE Y APELLIDO POR SEPARADO ---
        PatientProfileScreen(
            nameFromDb = userData!!.nombre,
            lastNameFromDb = userData!!.apellido, // <-- NUEVO
            phoneFromDb = userData!!.phone,
            emailFromDb = userData!!.email,
            modifier = modifier,
            onLogout = onLogout
        )
        // --- FIN CAMBIO 1 ---
    }
}
// --- FIN COMPOSABLE "INTELIGENTE" ---


// --- COMPOSABLE "TONTO" (PRESENTACIONAL) MODIFICADO ---
@Composable
private fun PatientProfileScreen(
    // --- CAMBIO 2: ACEPTAR NOMBRE Y APELLIDO POR SEPARADO ---
    nameFromDb: String,
    lastNameFromDb: String, // <-- NUEVO
    phoneFromDb: String,
    emailFromDb: String,
    // --- FIN CAMBIO 2 ---
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val headerColor = Color(0xFF4CB4B6)
    val sectionColor = Color(0xFF6FD2D4)

    // --- Estado para el formulario de perfil ---
    // --- CAMBIO 3: CREAR ESTADOS SEPARADOS ---
    var name by remember(nameFromDb) { mutableStateOf(nameFromDb) }
    var lastName by remember(lastNameFromDb) { mutableStateOf(lastNameFromDb) } // <-- NUEVO
    var phone by remember(phoneFromDb) { mutableStateOf(phoneFromDb) }
    var email by remember(emailFromDb) { mutableStateOf(emailFromDb) }
    // --- FIN CAMBIO 3 ---

    var isEditing by remember { mutableStateOf(false) }

    // --- CAMBIO 4: AÑADIR ESTADO DE ERROR PARA APELLIDO ---
    var nameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) } // <-- NUEVO
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    // --- FIN CAMBIO 4 ---

    var medicalHistory by remember { mutableStateOf("Paciente con alergia al polen. (Dato de prueba)") }
    var photoUriString by rememberSaveable { mutableStateOf<String?>("") }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    var showPictureSourceDialog by remember { mutableStateOf(false) }


    // --- Launchers para Cámara y Galería (Sin cambios) ---
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUriString = pendingCaptureUri?.toString()
            Toast.makeText(context, "Foto de perfil capturada", Toast.LENGTH_SHORT).show()
        } else {
            pendingCaptureUri = null
            Toast.makeText(context, "Captura cancelada", Toast.LENGTH_SHORT).show()
        }
    }
    val openCamera: () -> Unit = {
        val file = createTempImageFile(context)
        val uri = getImageUriFile(context, file)
        pendingCaptureUri = uri
        takePictureLauncher.launch(uri)
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(context, "Permiso de camara denegado", Toast.LENGTH_SHORT).show()
        }
    }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUriString = uri.toString()
            Toast.makeText(context, "Foto de perfil seleccionada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Selección cancelada", Toast.LENGTH_SHORT).show()
        }
    }
    // --- FIN LAUNCHERS ---


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9F9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- TARJETA DE CABECERA (Sin cambios) ---
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.profile_header_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = headerColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.Center) {
                        // --- FOTO DE PERFIL (Sin cambios) ---
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(headerColor.copy(alpha = 0.15f))
                                .clickable { showPictureSourceDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoUriString.isNullOrEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = stringResource(id = R.string.profile_edit_photo),
                                    tint = headerColor,
                                    modifier = Modifier.size(48.dp)
                                )
                            } else {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(Uri.parse(photoUriString))
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(headerColor.copy(alpha = 0.7f))
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                            )
                        }
                        // --- FIN FOTO DE PERFIL ---

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.profile_header_subtitle),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // --- Diálogo para elegir origen de la imagen (Sin cambios) ---
        if (showPictureSourceDialog) {
            ImageSourceDialog(
                onDismiss = { showPictureSourceDialog = false },
                onCameraSelected = {
                    showPictureSourceDialog = false
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        openCamera()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onGallerySelected = {
                    showPictureSourceDialog = false
                    pickImageLauncher.launch("image/*")
                },
                onDeleteSelected = {
                    showPictureSourceDialog = false
                    photoUriString = null
                    Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
                },
                showDeleteOption = !photoUriString.isNullOrEmpty()
            )
        }
        // --- FIN Diálogo ---


        // --- TARJETA DE DATOS PERSONALES ---
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(sectionColor.copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(headerColor)
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.profile_personal_info_title),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ProfileTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (isEditing && nameError != null) {
                            nameError = validateNamePart(it.trim(), "Nombre")
                        }
                    },
                    label = stringResource(id = R.string.profile_field_name),
                    enabled = isEditing,
                    error = if (isEditing) nameError else null
                )

                // --- CAMBIO 5: AÑADIR CAMPO DE APELLIDO ---
                ProfileTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        if (isEditing && lastNameError != null) {
                            lastNameError = validateNamePart(it.trim(), "Apellido")
                        }
                    },
                    label = "Apellido", // CRÍTICA: Deberías usar R.string.profile_field_lastname
                    enabled = isEditing,
                    error = if (isEditing) lastNameError else null
                )
                // --- FIN CAMBIO 5 ---

                ProfileTextField(
                    value = phone,
                    onValueChange = {
                        val digitsOnly = it.filter(Char::isDigit)
                        phone = digitsOnly
                        if (isEditing && phoneError != null) {
                            phoneError = validatePhoneDigitsOnly(digitsOnly)
                        }
                    },
                    label = stringResource(id = R.string.profile_field_phone),
                    enabled = isEditing,
                    error = if (isEditing) phoneError else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                ProfileTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (isEditing && emailError != null) {
                            emailError = validateEmail(it.trim())
                        }
                    },
                    label = stringResource(id = R.string.profile_field_email),
                    enabled = isEditing,
                    error = if (isEditing) emailError else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    Modifier,
                    DividerDefaults.Thickness,
                    color = headerColor.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // --- Sección de Notas Médicas (Sin cambios) ---
                Text(
                    text = "Notas Médicas (Historial)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = headerColor
                )
                Spacer(modifier = Modifier.height(8.dp))

                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledContainerColor = Color.Transparent,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                OutlinedTextField(
                    value = medicalHistory,
                    onValueChange = { medicalHistory = it },
                    label = { Text("Alergias, condiciones pre-existentes, etc.") },
                    enabled = isEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = textFieldColors
                )
            }
        }

        Button(
            onClick = {
                isEditing = true
                nameError = null
                lastNameError = null // <-- CAMBIO 6: LIMPIAR ERROR DE APELLIDO
                phoneError = null
                emailError = null
            },
            enabled = !isEditing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = stringResource(id = R.string.profile_edit_cta))
        }
        Button(
            onClick = {
                // --- CAMBIO 7: VALIDAR APELLIDO ANTES DE GUARDAR ---
                val cleanedName = name.trim()
                val cleanedLastName = lastName.trim() // <-- NUEVO
                val cleanedEmail = email.trim()

                val currentNameError = validateNamePart(cleanedName, "Nombre")
                val currentLastNameError = validateNamePart(cleanedLastName, "Apellido") // <-- NUEVO
                val currentPhoneError = validatePhoneDigitsOnly(phone)
                val currentEmailError = validateEmail(cleanedEmail)

                nameError = currentNameError
                lastNameError = currentLastNameError // <-- NUEVO
                phoneError = currentPhoneError
                emailError = currentEmailError

                val hasError = listOf(
                    currentNameError,
                    currentLastNameError, // <-- NUEVO
                    currentPhoneError,
                    currentEmailError
                ).any { it != null }
                // --- FIN CAMBIO 7 ---

                if (!hasError) {
                    name = cleanedName
                    lastName = cleanedLastName // <-- NUEVO
                    email = cleanedEmail
                    isEditing = false
                    Toast.makeText(
                        context,
                        context.getString(R.string.profile_save_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    // CRÍTICA: Aquí faltaría la llamada al VM:
                    // vm.updateUserData(name, lastName, phone, email, medicalHistory)
                }
            },
            enabled = isEditing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = stringResource(id = R.string.profile_save_cta))
        }
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = stringResource(id = R.string.common_logout))
        }
    }
}

// --- Diálogo para elegir origen de imagen (Sin cambios) ---
@Composable
private fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit,
    onDeleteSelected: () -> Unit,
    showDeleteOption: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Foto de Perfil") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextButton(onClick = onCameraSelected, modifier = Modifier.fillMaxWidth()) {
                    Text("Tomar foto con cámara")
                }
                TextButton(onClick = onGallerySelected, modifier = Modifier.fillMaxWidth()) {
                    Text("Seleccionar desde galería")
                }
                if (showDeleteOption) {
                    HorizontalDivider()
                    TextButton(onClick = onDeleteSelected, modifier = Modifier.fillMaxWidth()) {
                        Text("Eliminar foto actual", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

// --- Composable ProfileTextField (Sin cambios) ---
@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    error: String?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledBorderColor = MaterialTheme.colorScheme.outline,
        disabledContainerColor = Color.Transparent
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true,
        enabled = enabled,
        isError = error != null,
        colors = textFieldColors,
        supportingText = {
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

// --- Preview (Actualizada para pasar ambos) ---
@Preview(showBackground = true)
@Composable
private fun PatientProfileScreenPreview() {
    PatientProfileScreen(
        nameFromDb = "Carlos (Preview)",
        lastNameFromDb = "Sainz (Preview)", // <-- NUEVO
        phoneFromDb = "+56933333333",
        emailFromDb = "csainz@duoc.cl",
        onLogout = {}
    )
}