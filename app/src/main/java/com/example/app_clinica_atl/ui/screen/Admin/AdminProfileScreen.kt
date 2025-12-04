package com.example.app_clinica_atl.ui.screen.Admin

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.ui.viewmodel.AdminProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    viewModel: AdminProfileViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
            // Avatar Estático
            Box(modifier = Modifier.size(120.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.logo_clean), // Asegúrate de tener este recurso
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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