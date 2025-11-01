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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GroupAdd
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileViewModel
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
@Composable
fun DoctorProfileScreenVm(
    vm: DoctorProfileViewModel,
    onLogout: () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current
    val headerColor = MaterialTheme.colorScheme.primary

    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    var showPictureSourceDialog by remember { mutableStateOf(false) }

    // Mostrar mensaje de éxito al guardar
    if (uiState.saveSuccess) {
        Toast.makeText(context, "Perfil actualizado con éxito!", Toast.LENGTH_SHORT).show()
    }


    // --- Launchers para Cámara y Galería ---
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            vm.updatePhotoUri(pendingCaptureUri?.toString())
            Toast.makeText(context, "Foto capturada", Toast.LENGTH_SHORT).show()
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
        else Toast.makeText(context, "Permiso de camara denegado", Toast.LENGTH_SHORT).show()
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) vm.updatePhotoUri(uri.toString())
        else Toast.makeText(context, "Selección cancelada", Toast.LENGTH_SHORT).show()
    }

    Scaffold { padding ->
        // --- UI ---
        if (uiState.isLoading) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = headerColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding) // Aplicar el padding de Scaffold
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. TARJETA PRINCIPAL DEL DOCTOR
                DoctorInfoCard(
                    name = uiState.doctorInfo.name,
                    specialty = uiState.doctorInfo.specialty,
                    photoUriString = uiState.photoUriString,
                    headerColor = headerColor,
                    onEditPhotoClick = { showPictureSourceDialog = true }
                )

                // 2. MÉTRICAS DE DESEMPEÑO
                DoctorMetricsSection(
                    appointmentsCompleted = uiState.appointmentsCompleted,
                    newPatientsThisMonth = uiState.newPatientsThisMonth
                )

                // 3. INFORMACIÓN PROFESIONAL Y CONTACTO
                DoctorDetails(
                    info = uiState.doctorInfo,
                    onContactNumberChange = vm::updateContactNumber,
                    onAddressChange = vm::updateAddress,
                    onEmailChange = vm::updateEmail
                )

                // BOTÓN GUARDAR CAMBIOS (Conectado y con estados)
                Button(
                    onClick = vm::saveProfile, // <-- Conexión al VM
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Guardando...")
                    } else if (uiState.saveSuccess) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("¡Guardado!")
                    } else {
                        Text("Guardar Cambios")
                    }
                }

                // BOTÓN CERRAR SESIÓN (Conectado al handler pasado por NavGraph)
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


    // --- Diálogo para elegir origen de la imagen ---
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
                vm.updatePhotoUri(null)
                Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
            },
            showDeleteOption = !uiState.photoUriString.isNullOrEmpty()
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
                        model = ImageRequest.Builder(context).data(Uri.parse(photoUriString)).crossfade(true).build(),
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

@Composable
private fun DoctorMetricsSection(
    appointmentsCompleted: Int,
    newPatientsThisMonth: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MetricCard(
            title = "Citas Completadas",
            value = appointmentsCompleted.toString(),
            icon = Icons.Default.CalendarToday,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            title = "Pacientes Nuevos",
            value = newPatientsThisMonth.toString(),
            icon = Icons.Default.GroupAdd,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = title, style = MaterialTheme.typography.labelSmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
private fun DoctorDetails(
    info: DoctorInfo,
    onContactNumberChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
) {
    val consultationRateFormatted = try {
        val rate = info.consultationRate.toDouble()
        // Usa la configuración local chilena si es posible, sino usa un formato general
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
            ProfileTextField(value = info.contactNumber, onValueChange = onContactNumberChange, label = "Teléfono", icon = Icons.Default.Phone)
            ProfileTextField(value = info.email, onValueChange = onEmailChange, label = "Email", enabled = true)
            ProfileTextField(value = info.address, onValueChange = onAddressChange, label = "Dirección", icon = Icons.Default.Place)
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
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        leadingIcon = if (icon != null) { { Icon(imageVector = icon, contentDescription = null) } } else null,
        enabled = enabled,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
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