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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_clinica_atl.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Funciones de Utilidad (fuera del Composable) ---

/**
 * Crea un archivo temporal para guardar la foto capturada por la cámara.
 */
private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    // Almacenamos el archivo en la caché de la app, en una subcarpeta 'images'
    val storageDir = File(context.cacheDir, "images").apply {
        if (!exists()) mkdirs() // Crea la carpeta si no existe
    }
    return File(storageDir, "IMG_$timeStamp.jpg") // Archivo temporal jpg
}

/**
 * Convierte un File a una Uri segura que la cámara puede usar (via FileProvider).
 */
private fun getImageUriFile(context: Context, file: File): Uri {
    // El 'authority' debe coincidir con el que definas en AndroidManifest.xml
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}


@Composable
fun PatientProfileScreen(
    modifier: Modifier = Modifier,
    onDeleteProfile: () -> Unit = {},
    onUploadMedicalHistory: () -> Unit = {}
) {
    val context = LocalContext.current
    val headerColor = Color(0xFF4CB4B6)
    val sectionColor = Color(0xFF6FD2D4)

    // --- Estado para el formulario de perfil (se llenará con datos reales del VM) ---
    var name by remember { mutableStateOf("Javier Soto") }
    var address by remember { mutableStateOf("Av. Siempre Viva 123") }
    var phone by remember { mutableStateOf("+56 9 1234 5678") }
    var email by remember { mutableStateOf("javier.soto@mail.com") }

    // --- Estado para la Foto de Perfil (DataStore) ---
    // En un proyecto real, esto vendría del ViewModel, leyendo DataStore.
    var photoUriString by rememberSaveable { mutableStateOf<String?>("") }
    // Uri temporal para la foto que se está capturando
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    var showPictureSourceDialog by remember { mutableStateOf(false) }


    // --- Launchers para Cámara y Galería ---

    // 1. Launcher para Abrir la Cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Si la foto se tomó correctamente, guardamos la Uri en el estado permanente
            photoUriString = pendingCaptureUri?.toString()
            Toast.makeText(context, "Foto de perfil capturada", Toast.LENGTH_SHORT).show()
        } else {
            // Si se cancela, limpiamos la Uri temporal
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

    // 2. Launcher para Abrir la Galería
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Si se seleccionó una imagen, la guardamos
            photoUriString = uri.toString()
            Toast.makeText(context, "Foto de perfil seleccionada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Selección cancelada", Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9F9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        // --- FOTO DE PERFIL (Cámara y Galería) ---
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(headerColor.copy(alpha = 0.15f))
                                .clickable { showPictureSourceDialog = true }, // Abre el diálogo al hacer clic
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoUriString.isNullOrEmpty()) {
                                // Muestra el ícono si no hay foto
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = stringResource(id = R.string.profile_edit_photo), // <-- CLAVE RENOMBRADA
                                    tint = headerColor,
                                    modifier = Modifier.size(48.dp)
                                )
                            } else {
                                // Muestra la foto si existe
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
                            // Icono para indicar que se puede editar/añadir foto
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
                    IconButton(onClick = onDeleteProfile) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.profile_delete_cta),
                            tint = Color(0xFFB00020)
                        )
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
                    pickImageLauncher.launch("image/*") // Lanza la galería
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
                    onValueChange = { name = it },
                    label = stringResource(id = R.string.profile_field_name)
                )
                ProfileTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = stringResource(id = R.string.profile_field_address)
                )
                ProfileTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = stringResource(id = R.string.profile_field_phone)
                )
                ProfileTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(id = R.string.profile_field_email)
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    Modifier,
                    DividerDefaults.Thickness,
                    color = headerColor.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.profile_upload_section_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = headerColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(sectionColor.copy(alpha = 0.35f))
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.profile_upload_placeholder),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            tint = headerColor,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = onUploadMedicalHistory) {
                            Text(text = stringResource(id = R.string.profile_upload_cta))
                        }
                    }
                }
            }
        }

        Button(
            onClick = { /* TODO: handle save */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = stringResource(id = R.string.profile_save_cta))
        }
    }
}

// --- Diálogo para elegir origen de imagen ---
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
                // Opción 1: Cámara
                TextButton(onClick = onCameraSelected, modifier = Modifier.fillMaxWidth()) {
                    Text("Tomar foto con cámara")
                }
                // Opción 2: Galería
                TextButton(onClick = onGallerySelected, modifier = Modifier.fillMaxWidth()) {
                    Text("Seleccionar desde galería")
                }
                // Opción 3: Eliminar (solo si hay foto)
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
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)

    )
}

@Preview(showBackground = true)
@Composable
private fun PatientProfileScreenPreview() {
    PatientProfileScreen()
}
