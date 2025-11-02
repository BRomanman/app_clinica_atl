package com.example.app_clinica_atl.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // <--- SOLUCIÓN ERROR 'Type androidx.compose.runtime.State has no method getValue'
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp // <--- SOLUCIÓN ERROR 'Unresolved reference dp'
import androidx.compose.ui.unit.sp // <--- SOLUCIÓN ERROR 'Unresolved reference sp'
import androidx.lifecycle.compose.collectAsStateWithLifecycle // <--- NECESARIO PARA EL VM
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel // <--- NECESARIO PARA INYECTAR EL VM


// --- FUNCIÓN PRINCIPAL CON VIEWMODEL (VM) ---
@Composable
fun HomeScreenVm(
    vm: AuthViewModel, // Recibe el ViewModel
    onBookAppointment: () -> Unit,
    onInsuranceSelected: () -> Unit
) {
    // 1. Obtener el nombre de usuario de forma reactiva y segura
    // Esto requiere el import de 'getValue'
    val displayName by vm.userDisplayName.collectAsStateWithLifecycle()

    HomeScreen(
        displayName = displayName, // Pasa el nombre al composable presentacional
        onBookAppointment = onBookAppointment,
        onInsuranceSelected = onInsuranceSelected
    )
}

// --- FUNCIÓN PRESENTACIONAL (Recibe el saludo) ---
@Composable
private fun HomeScreen(
    displayName: String, // Recibe el saludo formateado: "Hola Nombre (Rol)."
    onBookAppointment: () -> Unit,
    onInsuranceSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderSection(displayName) // Pasa el saludo
        MainActionCard(onBookAppointment)
        InsuranceSection(onInsuranceSelected = onInsuranceSelected)
    }
}

// --- SECCIÓN DE CABECERA (Muestra el saludo) ---
@Composable
private fun HeaderSection(displayName: String) { // Recibe el saludo
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
            // Muestra el saludo dinámico
            text = displayName,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private data class InsuranceHighlight(val imageRes: Int, val title: String)

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
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .width(200.dp)
        ) {
            Text(
                text = "Reserva tu Hora",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun InsuranceSection(onInsuranceSelected: () -> Unit) {
    val insuranceHighlights = listOf(
        InsuranceHighlight(
            imageRes = R.drawable.seguro_1,
            title = "Seguro de accidente vehicular"
        ),
        InsuranceHighlight(
            imageRes = R.drawable.seguro_vida_2,
            title = "Seguro de vida y salud"
        ),
        InsuranceHighlight(
            imageRes = R.drawable.seguro_salud_2,
            title = "Seguro de Salud Básico"
        ),
        InsuranceHighlight(
            imageRes = R.drawable.seguro_salud_1,
            title = "Seguro de Salud Avanzado"
        ),
        InsuranceHighlight(
            imageRes = R.drawable.familia_feliz1,
            title = "Seguro de Vida Familiar"
        ),
        InsuranceHighlight(
            imageRes = R.drawable.seguro_salud_3,
            title = "Seguro de Vida Senior"
        )
    )

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
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        insuranceHighlights.forEachIndexed { index, highlight ->
            InsuranceCard(
                imageRes = highlight.imageRes,
                title = highlight.title,
                onClick = onInsuranceSelected
            )
            if (index != insuranceHighlights.lastIndex) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun InsuranceCard(
    imageRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}