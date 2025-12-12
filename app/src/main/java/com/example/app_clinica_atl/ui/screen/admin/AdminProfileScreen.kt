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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val context = LocalContext.current

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
        if(uiState.updateSuccess) viewModel.clearMsg()
    }

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
                    model = profilePhotoUrl ?: DEFAULT_AVATAR_URL,
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

            // Formulario
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isEditing,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.apellido,
                onValueChange = viewModel::onApellidoChange,
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isEditing,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = {},
                label = { Text("Correo (No editable)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.telefono,
                onValueChange = viewModel::onTelefonoChange,
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isEditing,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mensajes y Botones
            if (uiState.errorMsg != null) {
                Text(uiState.errorMsg!!, color = MaterialTheme.colorScheme.error)
            }
            if (uiState.updateSuccess) {
                Text("¡Actualizado correctamente!", color = Color.Green)
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
                        enabled = !uiState.isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("Guardar")
                    }
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
