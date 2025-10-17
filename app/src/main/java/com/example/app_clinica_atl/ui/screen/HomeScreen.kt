package com.example.app_clinica_atl.ui.screen // <-- CORREGIDO

// (Importaciones de Compose)
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R

@Composable
fun HomeScreen(
    // Parámetro actualizado para la navegación
    onBookAppointment: () -> Unit
) {
    // Columna principal que permite el scroll vertical
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF0F0F0)) // Fondo gris claro
    ) {
        HeaderSection()
        MainActionCard(onBookAppointment) // Se le pasa la acción
        InsuranceSection()
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2C3E50)), // Fondo azul oscuro
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
            // TODO (MVVM): Conectar al nombre de usuario real desde un ViewModel
            text = "Hola Nombre_Usuario.",
            color = Color.White,
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
            onClick = onBookAppointment, // Acción de navegación
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF)), // Azul brillante
            modifier = Modifier
                .padding(bottom = 16.dp)
                .width(200.dp)
        ) {
            Text(text = "Reserva tu Hora", color = Color.White, fontSize = 16.sp)
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
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(16.dp))

        InsuranceCard(
            imageRes = R.drawable.seguro_1,
            title = "Seguro de accidente vehicular"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InsuranceCard(
            imageRes = R.drawable.seguro_vida_1,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                color = Color.DarkGray
            )
        }
    }
}