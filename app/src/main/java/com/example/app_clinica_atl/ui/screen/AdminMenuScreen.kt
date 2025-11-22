package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuScreen(
    onAddSpecialty: () -> Unit,
    onAddDoctor: () -> Unit,
    onViewDoctors: () -> Unit,
    onProfileClick: () -> Unit, // <--- ¡ESTE ES EL PARÁMETRO QUE FALTABA!
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador") },
                actions = {
                    // Botón de Perfil en la barra superior
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Mi Perfil")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Gestión de la Clínica", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onAddSpecialty, modifier = Modifier.fillMaxWidth()) {
                Text("Gestionar Especialidades")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onAddDoctor, modifier = Modifier.fillMaxWidth()) {
                Text("Agregar Nuevo Doctor")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onViewDoctors, modifier = Modifier.fillMaxWidth()) {
                Text("Ver Lista de Doctores")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón grande para "Mis Datos" también
            OutlinedButton(onClick = onProfileClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mis Datos")
            }
        }
    }
}