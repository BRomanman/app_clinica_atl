package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider // <-- Import añadido
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
// import androidx.compose.runtime.mutableStateOf // <-- Ya no se usan
// import androidx.compose.runtime.saveable.rememberSaveable // <-- Ya no se usan
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.data.local.especialidad.EspecialidadEntity
import com.example.app_clinica_atl.ui.viewmodel.AdminManageSpecialtiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageSpecialtiesScreen(
    viewModel: AdminManageSpecialtiesViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- ¡¡ESTADO LOCAL ELIMINADO!! ---
    // El ViewModel ahora maneja el estado de los campos.

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Especialidades") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- Formulario para Añadir ---
            Text("Añadir Nueva Especialidad", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // --- CAMPO "NOMBRE" CONECTADO AL VIEWMODEL ---
            OutlinedTextField(
                value = uiState.newSpecialtyName,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre (Ej: Cardiología)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.nameError != null // Muestra error si existe
            )
            // Muestra el error en tiempo real
            uiState.nameError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- CAMPO "PRECIO" CONECTADO AL VIEWMODEL ---
            OutlinedTextField(
                value = uiState.newSpecialtyPrice,
                onValueChange = viewModel::onPriceChange,
                label = { Text("Precio (Ej: 25000)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = uiState.priceError != null // Muestra error si existe
            )
            // Muestra el error en tiempo real
            uiState.priceError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- BOTÓN CONECTADO AL VIEWMODEL ---
            Button(
                onClick = viewModel::addSpecialty, // Llama a la función sin parámetros
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Especialidad")
            }

            // Muestra mensaje de error general (ej. "Nombre duplicado")
            uiState.errorMsg?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // --- Lista de Especialidades Existentes (Sin cambios) ---
            Text("Especialidades Actuales", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.specialties) { specialty ->
                        SpecialtyItem(
                            specialty = specialty,
                            onDelete = { viewModel.deleteSpecialty(specialty) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecialtyItem(
    specialty: EspecialidadEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(specialty.name, fontWeight = FontWeight.Bold)
                Text(
                    "\$${specialty.price.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}