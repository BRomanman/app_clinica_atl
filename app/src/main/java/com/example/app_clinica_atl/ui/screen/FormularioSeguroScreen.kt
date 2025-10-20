package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.app_clinica_atl.R

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
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(
                color = Color(0xFF57CBDD), // modificar color de fondo
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Formulario de Contratacion",
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            nombreError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }

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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            apellidoError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }

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
            fechaNacimientoError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    correoError = null
                },
                label = { Text("Correo electronico", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                isError = correoError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            correoError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            OutlinedTextField(
                value = telefono,
                onValueChange = {
                    telefono = it
                    telefonoError = null
                },
                label = { Text("Telefono", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                isError = telefonoError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            telefonoError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                text = "El usuario creado tendra una contrasena temporal compuesta por las dos primeras letras del nombre, el primer apellido y los ultimos dos digitos del telefono.",
                color = Color.Black
            )

            Button(
                onClick = {
                    when {
                        nombre.text.isBlank() -> nombreError = "El nombre es obligatorio."
                        apellido.text.isBlank() -> apellidoError = "El apellido es obligatorio."
                        !fechaNacimiento.text.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) ->
                            fechaNacimientoError = "La fecha debe tener formato dd/mm/aaaa."
                        !correo.text.contains("@") || !correo.text.contains(".") ->
                            correoError = "Correo invalido."
                        !telefono.text.matches(Regex("\\d{8,}")) ->
                            telefonoError = "El telefono debe contener al menos 8 numeros."
                        else -> {
                            nombreError = null
                            apellidoError = null
                            fechaNacimientoError = null
                            correoError = null
                            telefonoError = null
                            showConfirmationDialog = true
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

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text(text = stringResource(id = R.string.common_ok))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.insurance_form_confirmation_title))
            },
            text = {
                Text(text = stringResource(id = R.string.insurance_form_confirmation_body))
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormularioSeguroScreenPreview() {
    FormularioSeguroScreen(navController = rememberNavController())
}
