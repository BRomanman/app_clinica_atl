package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme // <-- ¡Importante!
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R

@Composable
fun HomeScreen(
    onBookAppointment: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            // --- CAMBIO: Usa el color de fondo del tema ---
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderSection()
        MainActionCard(onBookAppointment)
        InsuranceSection()
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // --- CAMBIO: Usa el color primario del tema ---
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la Clínica",
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Hola Nombre_Usuario.",
            // --- CAMBIO: Usa el color de texto "sobre primario" ---
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MainActionCard(onBookAppointment: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.familia_feliz1),
            contentDescription = "Familia en el parque",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Button(
            onClick = onBookAppointment,
            shape = RoundedCornerShape(8.dp),
            // --- CAMBIO: Usa el color secundario del tema ---
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .width(200.dp)
        ) {
            // --- CAMBIO: Usa el color de texto "sobre secundario" ---
            Text(
                text = "Reserva tu Hora",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun InsuranceSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Seguros",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            // --- CAMBIO: Usa el color de texto "sobre fondo" ---
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        InsuranceCard(
            imageRes = R.drawable.seguro_1,
            title = "Seguro de accidente vehicular"
        )
        Spacer(modifier = Modifier.height(16.dp))
        InsuranceCard(
            imageRes = R.drawable.seguro_vida_2,
            title = "Seguro de vida y salud"
        )
    }
}

@Composable
private fun InsuranceCard(imageRes: Int, title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        // --- CAMBIO: Define los colores de la Card desde el tema ---
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = title,
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                // --- CAMBIO: Usa el color de texto "sobre superficie" ---
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}