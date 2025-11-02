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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R

data class AdminPatientHistory(
    val name: String,
    val address: String,
    val contact: String,
    val email: String,
    val history: String
)

@Composable
fun AdminUserHistoriesScreen(
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }

    val histories = remember {
        listOf(
            AdminPatientHistory(
                name = "Keanu Reeves",
                address = "123 Matrix St, Hollywood, CA",
                contact = "+569 12345678",
                email = "keanu.reeves@example.com",
                history = "Paciente presenta alergia leve al polen. Historial de fractura de muneca en 2019. Todas las vacunas al dia."
            ),
            AdminPatientHistory(
                name = "Taylor Swift",
                address = "456 Music Row, Nashville, TN",
                contact = "+569 12345678",
                email = "taylor.swift@example.com",
                history = "Paciente bajo seguimiento por tratamiento preventivo. Sin hospitalizaciones recientes."
            ),
            AdminPatientHistory(
                name = "Diego Marquez",
                address = "Av. Providencia 456, Santiago",
                contact = "+56 9 3210 9876",
                email = "diego.marquez@example.cl",
                history = "Control de rutina anual completado. Se recomienda actividad fisica moderada."
            )
        )
    }

    val filteredHistories = histories.filter { history ->
        history.name.contains(query, ignoreCase = true) ||
                history.email.contains(query, ignoreCase = true)
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
            text = "Historiales de Usuarios",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Buscador") },
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
            items(filteredHistories) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFB7E0E5)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Informacion Personal",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AdminInfoRow(label = "Nombre:", value = item.name)
                        AdminInfoRow(label = "Dirección:", value = item.address)
                        AdminInfoRow(label = "Número de contacto:", value = item.contact)
                        AdminInfoRow(label = "Correo electrónico:", value = item.email)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Historial medico",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.history,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminInfoRow(label: String, value: String) {
    Text(
        text = "$label $value",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        lineHeight = 20.sp
    )
}
