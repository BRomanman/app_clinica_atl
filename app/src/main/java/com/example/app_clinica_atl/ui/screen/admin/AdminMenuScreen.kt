package com.example.app_clinica_atl.ui.screen.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuScreen(
    onAddSpecialty: () -> Unit,
    onAddDoctor: () -> Unit,
    onViewDoctors: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Mi Perfil")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesion")
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        tonalElevation = 4.dp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_clean),
                            contentDescription = "Logo clinica",
                            modifier = Modifier
                                .padding(6.dp)
                                .fillMaxWidth(0.35f)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Gestion de la Clinica", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Que deseas gestionar hoy?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminActionCard(
                    icon = Icons.Default.Add,
                    title = "Gestionar Especialidades",
                    subtitle = "Crea, edita o revisa especialidades",
                    onClick = onAddSpecialty
                )
                AdminActionCard(
                    icon = Icons.Default.MedicalServices,
                    title = "Agregar Nuevo Doctor",
                    subtitle = "Registra un nuevo profesional en la clinica",
                    onClick = onAddDoctor
                )
                AdminActionCard(
                    icon = Icons.Default.List,
                    title = "Ver Lista de Doctores",
                    subtitle = "Consulta y administra los doctores actuales",
                    onClick = onViewDoctors
                )
                AdminActionCard(
                    icon = Icons.Default.Person,
                    title = "Mis Datos",
                    subtitle = "Actualiza tu perfil y credenciales",
                    onClick = onProfileClick
                )
            }
        }
    }
}

@Composable
private fun AdminActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(10.dp))
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
