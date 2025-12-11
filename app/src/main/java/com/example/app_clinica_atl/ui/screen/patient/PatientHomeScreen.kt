package com.example.app_clinica_atl.ui.screen.patient

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import com.example.app_clinica_atl.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.WeatherInfo
import com.example.app_clinica_atl.ui.viewmodel.patient.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onBookAppointmentClick: () -> Unit,
    onInsuranceClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val weather = uiState.weather
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            HeroCard(
                userName = uiState.userName,
                onBookAppointmentClick = onBookAppointmentClick,
                onProfileClick = onProfileClick,
                onInsuranceClick = onInsuranceClick
            )
        }

        item {
            WeatherCard(
                weather = weather,
                isLoading = uiState.isWeatherLoading,
                error = uiState.weatherError
            )
        }

        item {
            DoctorsCarousel(doctors = uiState.popularDoctors)
        }
    }
}




@Composable
private fun HeroCard(
    userName: String,
    onBookAppointmentClick: () -> Unit,
    onProfileClick: () -> Unit,
    onInsuranceClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .padding(22.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hola, ${userName.ifBlank { "usuario" }}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    TextButton(onClick = onProfileClick) {
                        Text(
                            text = "Mi perfil",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillTag(text = "Citas 24/7")
                    PillTag(text = "Doctores verificados")
                }

                Image(
                    painter = painterResource(id = R.drawable.atencion_1),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Button(
                    onClick = onBookAppointmentClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .height(44.dp)
                ) {
                    Text("Agendar consulta", fontSize = 15.sp)
                }

                Button(
                    onClick = onInsuranceClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .height(44.dp)
                ) {
                    Text("Ver seguros disponibles", fontSize = 15.sp)
                }
            }
        }
    }
}


@Composable
fun DoctorsCarousel(doctors: List<UsuarioDto>) {
    val imagePool = listOf(
        R.drawable.doctor_cardio_1,
        R.drawable.doctor_cardio_2,
        R.drawable.doctor_cardio_3,
        R.drawable.doctor_cardio_4,
        R.drawable.doctor_cardio_5,
        R.drawable.doctor_derma_1,
        R.drawable.doctor_derma_2,
        R.drawable.doctor_derma_3,
        R.drawable.doctor_derma_4,
        R.drawable.doctor_derma_5,
        R.drawable.doctor_medgen_1,
        R.drawable.doctor_medgen_2,
        R.drawable.doctor_medgen_3,
        R.drawable.doctor_medgen_4,
        R.drawable.doctor_medgen_5,
        R.drawable.doctor_nutri_1,
        R.drawable.doctor_nutri_2,
        R.drawable.doctor_nutri_3,
        R.drawable.doctor_nutri_4,
        R.drawable.doctor_pedi_1,
        R.drawable.doctor_pedi_2,
        R.drawable.doctor_pedi_3,
        R.drawable.doctor_pedi_4,
        R.drawable.doctor_psico_1,
        R.drawable.doctor_psico_2,
        R.drawable.doctor_psico_3,
        R.drawable.doctor_psico_4,
        R.drawable.doctor_psico_5
    )

    val assignedImages by remember(doctors) {
        mutableStateOf(
            doctors.associate { it.id to imagePool.shuffled().first() }
        )
    }

    val doctorCards = doctors.take(6).map { doctor ->
        DoctorCardData(
            name = doctor.name.ifBlank { "Doctor/a" },
            specialty = doctor.specialty?.ifBlank { "Especialidad no disponible" } ?: "Especialidad no disponible",
            imageRes = assignedImages[doctor.id] ?: imagePool.random()
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Doctores Populares",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        if (doctorCards.isEmpty()) {
            Text(
                text = "No hay doctores disponibles.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(doctorCards) { doctor ->
                    DoctorCard(
                        doctor = doctor
                    )
                }
            }
        }
    }
}

@Composable
private fun DoctorCard(doctor: DoctorCardData) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(220.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = doctor.imageRes),
                contentDescription = "Doctor",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                        )
                )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = doctor.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = doctor.specialty,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

private data class DoctorCardData(
    val name: String,
    val specialty: String,
    val imageRes: Int
)


@Composable
private fun PillTag(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onPrimary,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun WeatherCard(
    weather: WeatherInfo?,
    isLoading: Boolean,
    error: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Text(
                        text = "Cargando clima...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                error != null -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "No pudimos obtener el clima.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                weather != null -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Clima en ${weather.locationLabel}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "${weather.temperatureC}°C",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = weather.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Viento: ${weather.windKmh} km/h",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = "Ten un muy buen dia \uD83D\uDE0E",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
