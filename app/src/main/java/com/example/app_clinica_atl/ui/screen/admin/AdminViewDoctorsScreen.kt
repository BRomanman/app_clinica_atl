package com.example.app_clinica_atl.ui.screen.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
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
                                val photoUrl = resolveDoctorPhotoUrl(doctor)
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(photoUrl)
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(R.drawable.ic_person_placeholder),
                                    error = painterResource(R.drawable.ic_person_placeholder),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
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

private fun resolveDoctorPhotoUrl(doctor: DoctorDto): String? {
    val candidate = doctor.usuario?.imagenPerfil?.takeIf { it.isNotBlank() }
    if (!candidate.isNullOrBlank()) return ensureAbsoluteUrl(candidate)
    val doctorId = doctor.id ?: doctor.usuario?.doctor?.id ?: doctor.usuario?.trabajador?.id
    return doctorId?.let { "${RetrofitClient.BASE_URL_USUARIO}doctores/$it/foto-perfil" }
}

private fun ensureAbsoluteUrl(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.startsWith("http", ignoreCase = true)) return trimmed
    val base = RetrofitClient.BASE_URL_USUARIO.trimEnd('/')
    return "$base/${trimmed.trimStart('/')}"
}
