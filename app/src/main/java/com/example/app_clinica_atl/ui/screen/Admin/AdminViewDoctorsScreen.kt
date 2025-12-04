package com.example.app_clinica_atl.ui.screen.Admin

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.ui.viewmodel.AdminViewDoctorsViewModel

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
                // Lista
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.filteredList) { doctor ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDoctorClick(doctor.id) },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Person, null, Modifier.size(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(doctor.name, style = MaterialTheme.typography.titleMedium)
                                    Text(doctor.specialty ?: "Sin especialidad", style = MaterialTheme.typography.bodyMedium)
                                    Text(doctor.email, style = MaterialTheme.typography.bodySmall)
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