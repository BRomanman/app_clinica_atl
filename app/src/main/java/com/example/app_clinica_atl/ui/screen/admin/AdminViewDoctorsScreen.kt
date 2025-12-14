package com.example.app_clinica_atl.ui.screen.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.ui.viewmodel.admin.AdminViewDoctorsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminViewDoctorsScreen(
    viewModel: AdminViewDoctorsViewModel,
    onBackClick: () -> Unit,
    onDoctorClick: (Long) -> Unit // Callback al hacer clic en un doctor
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Doctores") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // Buscador
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = { Text("Buscar (Nombre, Email)") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                val sortedDoctors = remember(uiState.filteredList) {
                    uiState.filteredList.sortedBy { (it.nombre ?: it.usuario?.nombre).orEmpty().lowercase() }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sortedDoctors) { doctor ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    doctor.id?.let { onDoctorClick(it) }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Person, null, Modifier.size(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(Modifier.fillMaxWidth()) {
                                    val displayName = listOfNotNull(doctor.nombre, doctor.apellido)
                                        .joinToString(" ")
                                        .trim()
                                        .takeIf { it.isNotBlank() }
                                        ?: listOfNotNull(doctor.usuario?.nombre, doctor.usuario?.apellido)
                                            .joinToString(" ")
                                            .trim()
                                            .ifBlank { "Doctor" }
                                    val email = doctor.usuario?.correo ?: doctor.correo.orEmpty()


                                    Text(
                                        displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = doctor.especialidad ?: "Sin especialidad",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )


                                        Spacer(modifier = Modifier.weight(1f))


                                        doctor.id?.let { id ->
                                            Text(
                                                text = "ID: $id",
                                                style = MaterialTheme.typography.titleLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }


                                    Text(
                                        email,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                if (uiState.filteredList.isEmpty() && !uiState.isLoading) {
                    Text("No hay doctores.", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}
