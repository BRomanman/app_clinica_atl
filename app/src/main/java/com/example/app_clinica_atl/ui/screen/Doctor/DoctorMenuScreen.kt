package com.example.app_clinica_atl.ui.screen.Doctor

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorMenuScreen(
    onProfileClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onSearchPatient: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // IMAGEN ARRIBA DEL MENÚ
            Image(
                painter = painterResource(id = R.drawable.logo_clean),
                contentDescription = "Doctor",
                modifier = Modifier
                    .height(120.dp))

            Spacer(modifier = Modifier.height(52.dp))

            Text("Menú del Doctor", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(100.dp))





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

            Spacer(modifier = Modifier.height(60.dp))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}