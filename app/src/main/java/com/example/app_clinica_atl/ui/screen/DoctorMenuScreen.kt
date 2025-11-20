package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorMenuScreen(
    onProfileClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onSearchPatient: () -> Unit,
    onLogout: () -> Unit // <-- ¡¡PARÁMETRO AÑADIDO!!
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
            Button(onClick = onSearchPatient, modifier = Modifier.fillMaxWidth()) {
                Text("Buscar Pacientes por ID")
            }

            // --- ¡¡BOTÓN DE LOGOUT AÑADIDO!! ---
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}