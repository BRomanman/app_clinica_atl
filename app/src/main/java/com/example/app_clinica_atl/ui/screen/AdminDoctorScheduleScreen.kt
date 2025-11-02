package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R

data class AdminDoctorSchedule(
    val doctorName: String,
    val specialty: String,
    val schedules: List<String>
)

@Composable
fun AdminDoctorScheduleScreen(
    modifier: Modifier = Modifier
) {
    val sampleSchedules = remember {
        listOf(
            AdminDoctorSchedule(
                doctorName = "Dra. Ana Torres",
                specialty = "Cardiología",
                schedules = listOf("Lunes 09:00 - 13:00", "Miércoles 14:00 - 18:00", "Viernes 09:00 - 12:00")
            ),
            AdminDoctorSchedule(
                doctorName = "Dr. Luis Martinez",
                specialty = "Pediatría",
                schedules = listOf("Martes 08:30 - 12:30", "Jueves 10:00 - 16:00")
            ),
            AdminDoctorSchedule(
                doctorName = "Dra. Camila Ruiz",
                specialty = "Dermatología",
                schedules = listOf("Lunes 15:00 - 19:00", "Sábado 09:00 - 13:00")
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Clinica",
            modifier = Modifier.height(90.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Horarios por Doctor",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleSchedules) { schedule ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = schedule.doctorName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = schedule.specialty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        schedule.schedules.forEach { slot ->
                            Text(
                                text = "- $slot",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}
