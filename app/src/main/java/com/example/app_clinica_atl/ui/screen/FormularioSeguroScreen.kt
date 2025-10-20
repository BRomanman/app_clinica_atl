package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioSeguroScreen(navController: NavController) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var apellido by remember { mutableStateOf(TextFieldValue("")) }
    var fechaNacimiento by remember { mutableStateOf(TextFieldValue("")) }
    var correo by remember { mutableStateOf(TextFieldValue("")) }
    var telefono by remember { mutableStateOf(TextFieldValue("")) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        cursorColor = Color.Black,
        focusedIndicatorColor = Color(0xFF2196F3),
        unfocusedIndicatorColor = Color.Gray,
        focusedLabelColor = Color(0xFF2196F3),
        unfocusedLabelColor = Color.DarkGray
    )

    // Regex que acepta letras, tildes, diéresis, ñ y espacios
    val letrasRegex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]*$")

    Scaffold(containerColor = Color(0xFFF5F5F5)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Formulario de Contratación",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )
            Text(
                text = "Completa tus datos para continuar con el proceso.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            // NOMBRE: acepta letras con acentos y espacios
            OutlinedTextField(
                value = nombre,
                onValueChange = { input ->
                    val filtrado = input.text.filter { it.toString().matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]")) }
                    nombre = input.copy(
                        text = filtrado,
                        selection = androidx.compose.ui.text.TextRange(filtrado.length)
                    )
                },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            // APELLIDO: igual que nombre
            OutlinedTextField(
                value = apellido,
                onValueChange = { input ->
                    val filtrado = input.text.filter { it.toString().matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]")) }
                    apellido = input.copy(
                        text = filtrado,
                        selection = androidx.compose.ui.text.TextRange(filtrado.length)
                    )
                },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            // FECHA: máximo 10 caracteres (DD/MM/YYYY)
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { input ->
                    val texto = input.text.take(10)
                    fechaNacimiento = input.copy(
                        text = texto,
                        selection = androidx.compose.ui.text.TextRange(texto.length)
                    )
                },
                label = { Text("Fecha de nacimiento (DD/MM/YYYY)") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // CORREO
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // TELÉFONO: solo números, máximo 9 dígitos
            OutlinedTextField(
                value = telefono,
                onValueChange = { input ->
                    val soloNumeros = input.text.filter { it.isDigit() }.take(9)
                    telefono = input.copy(
                        text = soloNumeros,
                        selection = androidx.compose.ui.text.TextRange(soloNumeros.length)
                    )
                },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showConfirmationDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Confirmar seguro")
            }
        }
    }

    // Diálogo de confirmación
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            confirmButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Entendido")
                }
            },
            title = { Text("Tus datos se han enviado correctamente.") },
            text = { Text("Nos contactaremos contigo para confirmar la solicitud.") }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormularioSeguroScreenPreview() {
    FormularioSeguroScreen(navController = rememberNavController())
}
