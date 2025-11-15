package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla de Menú para el Doctor - ¡ACTUALIZADA!
 * Ahora acepta el callback 'onSearchPatient'.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorMenuScreen(
    onProfileClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onSearchPatient: () -> Unit // <-- ¡¡AQUÍ ESTÁ LA SOLUCIÓN!!
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Portal del Doctor") })
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
            Text("Menú del Doctor", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onProfileClick, modifier = Modifier.fillMaxWidth()) {
                Text("Ver mi Perfil")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onScheduleClick, modifier = Modifier.fillMaxWidth()) {
                Text("Ver mi Agenda")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botón que usa el nuevo callback
            Button(onClick = onSearchPatient, modifier = Modifier.fillMaxWidth()) {
                Text("Buscar Pacientes")
            }
        }
    }
}