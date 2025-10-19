package com.example.atl_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun FormularioSeguroScreen(navController: NavController) {
    var nombre by remember { mutableStateOf(TextFieldValue()) }
    var apellido by remember { mutableStateOf(TextFieldValue()) }
    var fechaNacimiento by remember { mutableStateOf(TextFieldValue()) }
    var correo by remember { mutableStateOf(TextFieldValue()) }
    var telefono by remember { mutableStateOf(TextFieldValue()) }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var apellidoError by remember { mutableStateOf<String?>(null) }
    var fechaNacimientoError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(
                color = Color(0xFF57CBDD),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Formulario de Contratación",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black
            )

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = null
                },
                label = { Text("Nombre", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                isError = nombreError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text         // Teclado de texto
                ),
            )
            if (nombreError != null) {
                Text(
                    text = nombreError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Campo Apellido
            OutlinedTextField(
                value = apellido,
                onValueChange = {
                    apellido = it
                    apellidoError = null
                },
                label = { Text("Apellido", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                isError = apellidoError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text         // Teclado de texto
                )
            )
            if (apellidoError != null) {
                Text(
                    text = apellidoError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Campo Fecha de Nacimiento
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = {
                    fechaNacimiento = it
                    fechaNacimientoError = null
                },
                label = { Text("Fecha de nacimiento (dd/mm/aaaa)", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                isError = fechaNacimientoError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            if (fechaNacimientoError != null) {
                Text(
                    text = fechaNacimientoError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Campo Correo
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    correoError = null
                },
                label = { Text("Correo electrónico", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                isError = correoError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email)
            )
            if (correoError != null) {
                Text(
                    text = correoError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Campo Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = {
                    telefono = it
                    telefonoError = null
                },
                label = { Text("Teléfono", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                isError = telefonoError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            if (telefonoError != null) {
                Text(
                    text = telefonoError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                text = "El siguiente formulario creará un usuario para el solicitante con una contraseña estandar. Esta contraseña contendrá las primeras 2 letras de su nombre seguido de su primer apellido más los últimos 2 digitos del teléfono.",
                color = Color.Black
            )

            Button(
                onClick = {
                    // Validaciones individuales con mensajes de error específicos
                    when {
                        nombre.text.isBlank() -> nombreError = "El nombre es obligatorio."
                        apellido.text.isBlank() -> apellidoError = "El apellido es obligatorio."
                        !fechaNacimiento.text.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) -> fechaNacimientoError = "La fecha debe tener formato dd/mm/aaaa."
                        !correo.text.contains("@") || !correo.text.contains(".") -> correoError = "Correo inválido."
                        !telefono.text.matches(Regex("\\d{8,}")) -> telefonoError = "El teléfono debe tener al menos 8 dígitos numéricos."
                        else -> {
                            // Limpiar todos los errores y navegar
                            nombreError = null
                            apellidoError = null
                            fechaNacimientoError = null
                            correoError = null
                            telefonoError = null
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text("Confirmar Seguro")
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun FormularioSeguroScreenPreview() {
    FormularioSeguroScreen(navController = rememberNavController())
}