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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@Composable
fun AdminAddDoctorScreen(
    onCreateDoctor: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var bonus by remember { mutableStateOf("") }
    var specialtyId by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
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
            text = "Agregar Doctor",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))

        AdminSectionHeader(title = "Informacion Personal")

        Spacer(modifier = Modifier.height(20.dp))

        AdminLabeledField(
            label = "Nombre",
            value = firstName,
            onValueChange = { firstName = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AdminLabeledField(
            label = "Apellido",
            value = lastName,
            onValueChange = { lastName = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AdminLabeledField(
            label = "Fecha de Nacimiento",
            value = birthDate,
            onValueChange = { birthDate = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AdminLabeledField(
            label = "Correo Electronico",
            value = email,
            onValueChange = { email = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AdminLabeledField(
            label = "Telefono",
            value = phone,
            onValueChange = { phone = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AdminLabeledField(
            label = "Contrasena",
            value = password,
            onValueChange = { password = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AdminLabeledField(
            label = "Tarifa Consulta",
            value = rate,
            onValueChange = { rate = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AdminLabeledField(
            label = "Sueldo",
            value = salary,
            onValueChange = { salary = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AdminLabeledField(
            label = "Bono",
            value = bonus,
            onValueChange = { bonus = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AdminLabeledField(
            label = "ID Especialidad",
            value = specialtyId,
            onValueChange = { specialtyId = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCreateDoctor,
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF27AE60),
                contentColor = Color.White
            )
        ) {
            Text(text = "Agregar Doctor", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
