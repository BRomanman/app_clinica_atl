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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.layout.ContentScale // <-- ¡IMPORT AÑADIDO!
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // <-- ¡¡IMPORT DE COIL AÑADIDO!!
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
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ¡¡ENCABEZADO DE BIENVENIDA ACTUALIZADO!! ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                // ¡CAMBIO! Usamos AsyncImage para cargar la foto de perfil
                AsyncImage(
                    model = uiState.profileImageUrl, // <-- Carga la URL de la BD
                    contentDescription = "Foto de perfil",
                    placeholder = painterResource(id = R.drawable.goku_perfil), // Tu placeholder
                    error = painterResource(id = R.drawable.goku_perfil), // Tu placeholder si falla
                    contentScale = ContentScale.Crop, // Escala la imagen
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                )
            }
        }

        // --- Botón principal de Agendar Cita (al final) ---
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBookAppointmentClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Reserva tu Hora",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
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

        // --- Botón principal de Agendar Cita (sin cambios) ---
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBookAppointmentClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Reserva tu Hora",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ... (El resto del archivo: InsuranceCardItem, InsuranceCardDisplay)
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
