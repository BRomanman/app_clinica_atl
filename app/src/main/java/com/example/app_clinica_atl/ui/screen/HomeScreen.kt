package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale // <-- ¡IMPORT AÑADIDO!
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onBookAppointmentClick: () -> Unit,
    onInsuranceClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.surface
        )
    )

    val bannerImages = listOf(
        R.drawable.familia_feliz1,
        R.drawable.clinica_1,
        R.drawable.clinica_2
    )

    val insuranceDisplayCards = listOf(
        InsuranceCardDisplay(
            title = "Seguro de accidente vehicular",
            imageRes = R.drawable.seguro_salud_1
        ),
        InsuranceCardDisplay(
            title = "Seguro de vida familiar",
            imageRes = R.drawable.seguro_vida_1
        ),
        InsuranceCardDisplay(
            title = "Seguro de hospitalización",
            imageRes = R.drawable.seguro_empresarial1
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ¡¡ENCABEZADO DE BIENVENIDA ACTUALIZADO!! ---
        item {
            HeroCard(
                userName = uiState.userName,
                onBookAppointmentClick = onBookAppointmentClick
            )
        }

        // --- Carrusel de imágenes (sin cambios) ---
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(bannerImages) { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillParentMaxWidth(0.9f)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }

        // --- Título para "Nuestros Seguros" (sin cambios) ---
        item {
            Text(
                text = "Nuestros Seguros",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                textAlign = TextAlign.Start
            )
        }

        // --- Lista de tarjetas de Seguros (sin cambios) ---
        items(insuranceDisplayCards) { card ->
            InsuranceCardItem(
                title = card.title,
                imageRes = card.imageRes,
                onClick = onInsuranceClick
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            QuickActionsRow(
                onBookAppointmentClick = onBookAppointmentClick,
                onInsuranceClick = onInsuranceClick,
                onProfileClick = onProfileClick
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeroCard(
    userName: String,
    onBookAppointmentClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Hola, $userName",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "Agenda tu próxima atención en segundos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Button(
                    onClick = onBookAppointmentClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text("Agendar ahora")
                }
            }
            Image(
                painter = painterResource(id = R.drawable.doctor_medgen_1),
                contentDescription = null,
                modifier = Modifier
                    .height(110.dp)
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onBookAppointmentClick: () -> Unit,
    onInsuranceClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(title = "Agendar", subtitle = "Reserva en 3 pasos", onClick = onBookAppointmentClick)
        QuickActionCard(title = "Seguros", subtitle = "Revisa tus planes", onClick = onInsuranceClick)
        QuickActionCard(title = "Perfil", subtitle = "Tu información", onClick = onProfileClick)
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}


@Composable
private fun InsuranceCardItem(
    title: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary, // Ajusta el color si es necesario
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

data class InsuranceCardDisplay(
    val title: String,
    val imageRes: Int
)
