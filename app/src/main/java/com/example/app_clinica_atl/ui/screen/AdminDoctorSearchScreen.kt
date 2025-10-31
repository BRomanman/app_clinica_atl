package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchViewModel

@Composable
fun AdminDoctorSearchScreenVm(
    vm: DoctorSearchViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    AdminDoctorSearchScreen(
        searchText = uiState.searchText,
        doctors = uiState.doctors,
        onSearchChange = vm::onSearchTextChange,
        modifier = modifier
    )
}

@Composable
private fun AdminDoctorSearchScreen(
    searchText: String,
    doctors: List<DoctorInfo>,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
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
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchChange,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Buscar por ID, nombre, especialidad o correo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
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
            items(doctors, key = { it.id }) { doctor ->
                DoctorCard(doctor = doctor)
            }
        }
    }
}

@Composable
private fun DoctorCard(doctor: DoctorInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB7E0E5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = doctor.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            AdminDoctorInfoRow(label = "ID:", value = doctor.id)
            AdminDoctorInfoRow(label = "Especialidad:", value = doctor.specialty)
            AdminDoctorInfoRow(label = "Contacto:", value = doctor.contactNumber)
            AdminDoctorInfoRow(label = "Correo:", value = doctor.email)
            AdminDoctorInfoRow(label = "Disponibilidad:", value = doctor.availability)
        }
    }
}

@Composable
private fun AdminDoctorInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.4f)
        )
        Box(
            modifier = Modifier
                .weight(0.6f)
                .background(Color.White, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = value.ifBlank { "-" },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}
