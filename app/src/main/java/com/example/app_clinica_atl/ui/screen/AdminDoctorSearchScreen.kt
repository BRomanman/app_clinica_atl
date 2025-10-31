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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R

data class AdminDoctorProfile(
    val id: String,
    val name: String,
    val specialty: String,
    val contact: String,
    val email: String,
    val availability: String
)

@Composable
fun AdminDoctorSearchScreen(
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }

    val doctors = remember {
        listOf(
            AdminDoctorProfile(
                id = "1",
                name = "Dra. Sofia Morales",
                specialty = "Cardiologia",
                contact = "+56 2 2345 6789",
                email = "sofia.morales@atlclinic.cl",
                availability = "Lunes a Jueves, 09:00 - 16:00"
            ),
            AdminDoctorProfile(
                id = "2",
                name = "Dr. Javier Delgado",
                specialty = "Traumatologia",
                contact = "+56 9 8765 4321",
                email = "javier.delgado@atlclinic.cl",
                availability = "Martes a Viernes, 10:00 - 18:00"
            ),
            AdminDoctorProfile(
                id = "3",
                name = "Dra. Emilia Rios",
                specialty = "Neurologia",
                contact = "+56 2 2100 9988",
                email = "emilia.rios@atlclinic.cl",
                availability = "Lunes, Miercoles y Viernes, 08:30 - 15:30"
            )
        )
    }

    val filteredDoctors = doctors.filter { doctor ->
        doctor.id.contains(query, ignoreCase = true) ||
                doctor.name.contains(query, ignoreCase = true) ||
                doctor.specialty.contains(query, ignoreCase = true) ||
                doctor.email.contains(query, ignoreCase = true)
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
            text = "Directorio de Doctores",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Buscar por ID, nombre, especialidad o correo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(filteredDoctors) { doctor ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFB7E0E5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = doctor.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AdminDoctorInfoRow(label = "ID:", value = doctor.id)
                        Spacer(modifier = Modifier.height(4.dp))
                        AdminDoctorInfoRow(label = "Especialidad:", value = doctor.specialty)
                        AdminDoctorInfoRow(label = "Contacto:", value = doctor.contact)
                        AdminDoctorInfoRow(label = "Correo:", value = doctor.email)
                        AdminDoctorInfoRow(label = "Disponibilidad:", value = doctor.availability)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminDoctorInfoRow(label: String, value: String) {
    Text(
        text = "$label $value",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        lineHeight = 20.sp
    )
}
