package com.example.app_clinica_atl.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validateFechaNacimiento
import com.example.app_clinica_atl.domain.validation.validateNamePart
import com.example.app_clinica_atl.domain.validation.validatePhoneDigitsOnly
import com.example.app_clinica_atl.notifications.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioSeguroScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf<String?>(null) }
    var apellido by remember { mutableStateOf("") }
    var apellidoError by remember { mutableStateOf<String?>(null) }
    var fechaNacimiento by remember { mutableStateOf("") }
    var fechaNacimientoError by remember { mutableStateOf<String?>(null) }
    var correo by remember { mutableStateOf("") }
    var correoError by remember { mutableStateOf<String?>(null) }
    var telefono by remember { mutableStateOf("") }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    var nombreTouched by remember { mutableStateOf(false) }
    var apellidoTouched by remember { mutableStateOf(false) }
    var fechaNacimientoTouched by remember { mutableStateOf(false) }
    var correoTouched by remember { mutableStateOf(false) }
    var telefonoTouched by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        cursorColor = Color.Black,
        focusedIndicatorColor = Color(0xFF2196F3),
        unfocusedIndicatorColor = Color.Gray,
        focusedLabelColor = Color(0xFF2196F3),
        unfocusedLabelColor = Color.DarkGray
    )

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(androidx.compose.foundation.layout.WindowInsets.ime)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
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

            OutlinedTextField(
                value = nombre,
                onValueChange = { value ->
                    nombre = value
                    nombreTouched = true
                    nombreError = validateNamePart(value.trim(), "Nombre")
                },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = nombreTouched && nombreError != null,
                supportingText = {
                    val error = nombreError
                    if (nombreTouched && error != null) {
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(
                value = apellido,
                onValueChange = { value ->
                    apellido = value
                    apellidoTouched = true
                    apellidoError = validateNamePart(value.trim(), "Apellido")
                },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = apellidoTouched && apellidoError != null,
                supportingText = {
                    val error = apellidoError
                    if (apellidoTouched && error != null) {
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { raw ->
                    val digits = raw.filter(Char::isDigit).take(8)
                    val formatted = buildString {
                        for ((index, char) in digits.withIndex()) {
                            append(char)
                            if (index == 1 || index == 3) append('-')
                        }
                    }
                    fechaNacimiento = formatted
                    fechaNacimientoTouched = true
                    fechaNacimientoError = when {
                        formatted.isBlank() -> "La fecha es obligatoria"
                        formatted.length < 10 -> "Completa la fecha en formato DD-MM-YYYY"
                        else -> validateFechaNacimiento(formatted)
                    }
                },
                label = { Text("Fecha de nacimiento (DD-MM-YYYY)") },
                placeholder = { Text("DD-MM-YYYY") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = fechaNacimientoTouched && fechaNacimientoError != null,
                supportingText = {
                    val error = fechaNacimientoError
                    if (fechaNacimientoTouched && error != null) {
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { value ->
                    correo = value
                    correoTouched = true
                    correoError = validateEmail(value.trim())
                },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = correoTouched && correoError != null,
                supportingText = {
                    val error = correoError
                    if (correoTouched && error != null) {
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { value ->
                    val digits = value.filter(Char::isDigit).take(15)
                    telefono = digits
                    telefonoTouched = true
                    telefonoError = validatePhoneDigitsOnly(digits)
                },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = telefonoTouched && telefonoError != null,
                supportingText = {
                    val error = telefonoError
                    if (telefonoTouched && error != null) {
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    nombreTouched = true
                    apellidoTouched = true
                    fechaNacimientoTouched = true
                    correoTouched = true
                    telefonoTouched = true

                    val nombreValidation = validateNamePart(nombre.trim(), "Nombre")
                    val apellidoValidation = validateNamePart(apellido.trim(), "Apellido")
                    val fechaValidation = validateFechaNacimiento(fechaNacimiento.trim())
                    val correoValidation = validateEmail(correo.trim())
                    val telefonoValidation = validatePhoneDigitsOnly(telefono.trim())

                    nombreError = nombreValidation
                    apellidoError = apellidoValidation
                    fechaNacimientoError = fechaValidation
                    correoError = correoValidation
                    telefonoError = telefonoValidation

                    val hasError = listOf(
                        nombreValidation,
                        apellidoValidation,
                        fechaValidation,
                        correoValidation,
                        telefonoValidation
                    ).any { it != null }

                    if (!hasError) {
                        val toastMessage = context.getString(R.string.insurance_form_toast_message)
                        Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()

                        val fullName = listOf(nombre.trim(), apellido.trim())
                            .filter { it.isNotBlank() }
                            .joinToString(" ")
                            .ifBlank { context.getString(R.string.insurance_notification_default_name) }

                        val hasPostNotificationPermission =
                            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                        if (hasPostNotificationPermission) {
                            NotificationHelper.showInsuranceConfirmation(
                                context = appContext,
                                policyHolderName = fullName
                            )
                        }
                    }

                    showConfirmationDialog = !hasError
                },
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

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            confirmButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text(stringResource(id = R.string.common_ok))
                }
            },
            title = { Text(stringResource(id = R.string.insurance_form_confirmation_title)) },
            text = { Text(stringResource(id = R.string.insurance_form_confirmation_body)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormularioSeguroScreenPreview() {
    FormularioSeguroScreen(navController = rememberNavController())
}
