package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla de Menú de Administrador.
 * Tiene las 2 funciones que pediste:
 * - Gestionar Especialidades
 * - Agregar Doctor
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuScreen(
    onAddSpecialty: () -> Unit,
    onAddDoctor: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
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

            Button(
                onClick = onAddSpecialty,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gestionar Especialidades")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddDoctor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar Nuevo Doctor")
            }
        }
    }
}