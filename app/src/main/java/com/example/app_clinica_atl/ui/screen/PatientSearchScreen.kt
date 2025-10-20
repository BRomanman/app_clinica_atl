package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_clinica_atl.data.model.Patient
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel

// 1. El Composable "Inteligente" (conecta con el VM)
@Composable
fun PatientSearchScreenVm(vm: PatientViewModel) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    PatientSearchScreen(
        searchText = uiState.searchText,
        patients = uiState.patients,
        onSearchChange = vm::onSearchTextChange
    )
}

// 2. El Composable "Tonto" (solo UI)
@Composable
private fun PatientSearchScreen(
    searchText: String,
    patients: List<Patient>,
    onSearchChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Usa el color de fondo de tu tema
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- BUSCADOR (Estilo de la foto) ---
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("buscador") }, // Texto de la foto
            leadingIcon = { Icon(Icons.Filled.Search, "Buscar") }, // Icono de la foto
            singleLine = true,
            colors = TextFieldDefaults.colors(
                // Usa los colores de tu tema
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // --- LISTA DE RESULTADOS ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(patients, key = { it.id }) { patient ->
                // Cada item de la lista es una "Ficha de Paciente"
                PatientInfoCard(patient = patient)
            }
        }
    }
}

// 3. La "Ficha" de información (basada en tu imagen)
@Composable
private fun PatientInfoCard(patient: Patient) {
    // Tomo el color teal de tu PatientProfileScreen, que coincide con la foto
    val sectionColor = Color(0xFF6FD2D4)
    val headerColor = Color(0xFF4CB4B6)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = sectionColor.copy(alpha = 0.5f) // Fondo teal
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- TÍTULO (Información Personal) ---
            Text(
                text = "Información Personal",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black, // Texto oscuro como en la foto
                fontWeight = FontWeight.Bold
            )

            // --- CAMPOS DE DATOS ---
            InfoRow("Nombre:", patient.nombre)
            InfoRow("direccion:", patient.direccion)
            InfoRow("numero de contacto:", patient.numeroContacto)
            InfoRow("correo electronico:", patient.correo)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = headerColor
            )

            // --- HISTORIAL MÉDICO (Estilo de la foto) ---
            Text(
                text = "historial medico",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = patient.historialMedico,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}

// Helper para mostrar una fila de "Etiqueta: Valor"
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge, // Un poco más grande
            color = Color.Black.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.4f) // 40% del espacio
        )
        // Esto simula el campo de texto blanco de tu foto
        Box(
            modifier = Modifier
                .weight(0.6f) // 60% del espacio
                .background(Color.White, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}