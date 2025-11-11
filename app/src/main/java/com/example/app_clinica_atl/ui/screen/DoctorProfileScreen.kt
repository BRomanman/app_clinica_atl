package com.example.app_clinica_atl.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
// --- 1. IMPORT ELIMINADO ---
// import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validatePhoneDigitsOnly
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri

// --- Funciones de Utilidad (Uris y Permisos) ---
private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply { if (!exists()) mkdirs() }
    return File(storageDir, "IMG_$timeStamp.jpg")
}

private fun getImageUriFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

// --- PANTALLA PRINCIPAL (VM) ---
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorProfileScreenVm(
    vm: AuthViewModel,
    onLogout: () -> Unit = {}
) {
    val doctorInfo by vm.currentDoctorInfo.collectAsStateWithLifecycle()
    val isSaving by vm.isSavingProfile.collectAsStateWithLifecycle()
    val saveSuccess by vm.saveProfileSuccess.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val headerColor = MaterialTheme.colorScheme.primary

    var photoUriString by remember(doctorInfo?.photoUri) { mutableStateOf(doctorInfo?.photoUri) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    var showPictureSourceDialog by remember { mutableStateOf(false) }

    var contactNumber by remember(doctorInfo) { mutableStateOf(doctorInfo?.contactNumber?.filter(Char::isDigit) ?: "") }
    var address by remember(doctorInfo) { mutableStateOf(doctorInfo?.address ?: "") }
    var email by remember(doctorInfo) { mutableStateOf(doctorInfo?.email ?: "") }

    var contactNumberError by remember(doctorInfo) { mutableStateOf<String?>(null) }
    var addressError by remember(doctorInfo) { mutableStateOf<String?>(null) }
    var emailError by remember(doctorInfo) { mutableStateOf<String?>(null) }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Perfil actualizado con éxito!", Toast.LENGTH_SHORT).show()
            vm.clearSaveDoctorProfileStatus()
        }
    }

    // --- Launchers para Cámara y Galería ---
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val newPhotoUri = pendingCaptureUri?.toString()
            photoUriString = newPhotoUri
            vm.updatePhotoUri(newPhotoUri)
            Toast.makeText(context, "Foto capturada", Toast.LENGTH_SHORT).show()
            pendingCaptureUri = null
        } else { pendingCaptureUri = null }
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
        if (isGranted) openCamera()
        else Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val newPhotoUri = uri.toString()
            photoUriString = newPhotoUri
            vm.updatePhotoUri(newPhotoUri)
        } else {
            Toast.makeText(context, "Selección cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold { padding ->
        // --- UI ---
        if (doctorInfo == null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = headerColor)
                Text(modifier = Modifier.padding(top = 80.dp), text = "Cargando perfil del doctor...")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 1. TARJETA PRINCIPAL DEL DOCTOR
                DoctorInfoCard(
                    name = doctorInfo!!.name,
                    specialty = doctorInfo!!.specialty,
                    photoUriString = photoUriString,
                    headerColor = headerColor,
                    onEditPhotoClick = { showPictureSourceDialog = true }
                )

                DoctorDetails(
                    info = doctorInfo!!,
                    contactNumber = contactNumber,
                    address = address,
                    email = email,
                    contactNumberError = contactNumberError,
                    addressError = addressError,
                    emailError = emailError,
                    onContactNumberChange = {
                        val digitsOnly = it.filter(Char::isDigit)
                        contactNumber = digitsOnly
                        contactNumberError = validatePhoneDigitsOnly(digitsOnly)
                    },
                    onAddressChange = {
                        address = it
                        addressError = validateAddressField(it)
                    },
                    onEmailChange = {
                        email = it
                        emailError = validateEmail(it.trim())
                    }
                )

                // BOTÓN GUARDAR CAMBIOS
                Button(
                    onClick = {
                        val cleanedEmail = email.trim()
                        val cleanedAddress = address.trim()

                        val phoneValidation = validatePhoneDigitsOnly(contactNumber)
                        val addressValidation = validateAddressField(cleanedAddress)
                        val emailValidation = validateEmail(cleanedEmail)

                        contactNumberError = phoneValidation
                        addressError = addressValidation
                        emailError = emailValidation

                        val hasError = listOf(phoneValidation, addressValidation, emailValidation).any { it != null }

                        if (!hasError) {
                            vm.saveDoctorProfile(
                                newContactNumber = contactNumber,
                                newAddress = cleanedAddress,
                                newEmail = cleanedEmail
                            )
                        } else {
                            Toast.makeText(context, "Revisa los campos marcados", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Guardando...")
                    } else if (saveSuccess) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("¡Guardado!")
                    } else {
                        Text("Guardar Cambios")
                    }
                }

                // BOTÓN CERRAR SESIÓN
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
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
    }


    if (showPictureSourceDialog) {
        ImageSourceDialog(
            onDismiss = { showPictureSourceDialog = false },
            onCameraSelected = {
                showPictureSourceDialog = false
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) openCamera()
                else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onGallerySelected = {
                showPictureSourceDialog = false
                pickImageLauncher.launch("image/*")
            },
            onDeleteSelected = {
                showPictureSourceDialog = false
                photoUriString = null
                vm.updatePhotoUri(null)
                Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
            },
            showDeleteOption = !photoUriString.isNullOrEmpty()
        )
    }
}

// --- COMPONENTES DE LA UI ---

@Composable
private fun DoctorInfoCard(
    name: String,
    specialty: String,
    photoUriString: String?,
    headerColor: Color,
    onEditPhotoClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Mi Perfil Profesional", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = headerColor)
            Spacer(modifier = Modifier.height(16.dp))

            // FOTO DE PERFIL
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(headerColor.copy(alpha = 0.15f))
                    .clickable(onClick = onEditPhotoClick),
                contentAlignment = Alignment.Center
            ) {
                if (photoUriString.isNullOrEmpty()) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Editar foto de perfil", tint = headerColor, modifier = Modifier.size(60.dp))
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(photoUriString.toUri()).crossfade(true).build(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(headerColor.copy(alpha = 0.7f))
                        .align(Alignment.BottomEnd)
                        .padding(6.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = headerColor)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.MedicalServices, contentDescription = "Especialidad", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = specialty, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

// --- 3. FUNCIONES ELIMINADAS ---
/*
@Composable
private fun DoctorMetricsSection(
    appointmentsCompleted: Int,
    newPatientsThisMonth: Int
) {
    // ... (código eliminado)
}

@Composable
private fun MetricCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    // ... (código eliminado)
}
*/

@Composable
private fun DoctorDetails(
    info: DoctorInfo,
    contactNumber: String,
    address: String,
    email: String,
    contactNumberError: String?,
    addressError: String?,
    emailError: String?,
    onContactNumberChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onEmailChange: (String) -> Unit
) {
    val consultationRateFormatted = try {
        val rate = info.consultationRate.toDouble()
        NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(rate)
    } catch (e: Exception) {
        "$ ${info.consultationRate}"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Información Profesional", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            // FILAS DE DATOS PROFESIONALES (No Editables)
            DetailRow(icon = Icons.Default.Work, label = "Antigüedad", value = "Desde ${info.since}")
            DetailRow(icon = Icons.Default.CalendarToday, label = "Disponibilidad", value = info.availability)
            DetailRow(icon = Icons.Default.AttachMoney, label = "Tarifa Consulta", value = consultationRateFormatted)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Text(text = "Información de Contacto", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))

            // CAMPOS EDITABLES
            ProfileTextField(
                value = contactNumber,
                onValueChange = onContactNumberChange,
                label = "Teléfono",
                icon = Icons.Default.Phone,
                error = contactNumberError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            ProfileTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                enabled = true,
                error = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            ProfileTextField(
                value = address,
                onValueChange = onAddressChange,
                label = "Dirección",
                icon = Icons.Default.Place,
                error = addressError
            )
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

// --- Componentes compartidos ---
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

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        leadingIcon = if (icon != null) { { Icon(imageVector = icon, contentDescription = null) } } else null,
        enabled = enabled,
        singleLine = true,
        isError = error != null,
        keyboardOptions = keyboardOptions,
        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

private fun validateAddressField(address: String): String? {
    val trimmed = address.trim()
    if (trimmed.isEmpty()) return "La dirección es obligatoria"
    if (trimmed.length < 5) return "La dirección debe tener al menos 5 caracteres"
    return null
}

@Preview(showBackground = true)
@Composable
private fun DoctorProfileScreenPreview() {
    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("Doctor Profile Screen (Preview de la UI, sin VM)")
        }
    }
}
