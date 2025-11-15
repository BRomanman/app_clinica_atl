package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.ui.viewmodel.HomeViewModel

/**
 * Pantalla principal (Home)
 * ¡SIN 'onProfileClick' y SIN 'Scaffold'!
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onBookAppointmentClick: () -> Unit,
    onMyDatesClick: () -> Unit,
    onInsuranceClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp), // Este padding es el de la pantalla
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado de bienvenida
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "¡Hola de nuevo,",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = uiState.userName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Image(
                painter = painterResource(id = R.drawable.goku_perfil), // Placeholder
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                // La navegación al perfil se hace desde el AppDrawer
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta principal
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "¿Cómo te sientes hoy?",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Agenda tu próxima cita médica.",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo_clean),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de acciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MenuButton(
                text = "Agendar Cita",
                icon = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplazar icono
                onClick = onBookAppointmentClick
            )
            MenuButton(
                text = "Mis Citas",
                icon = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplazar icono
                onClick = onMyDatesClick
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            MenuButton(
                text = "Seguros",
                icon = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplazar icono
                onClick = onInsuranceClick,
                modifier = Modifier.padding(start = 28.dp)
            )
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}