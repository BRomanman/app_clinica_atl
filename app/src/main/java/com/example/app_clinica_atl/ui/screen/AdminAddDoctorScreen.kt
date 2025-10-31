package com.example.app_clinica_atl.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validateFechaNacimiento
import com.example.app_clinica_atl.domain.validation.validateNamePart
import com.example.app_clinica_atl.domain.validation.validatePhoneDigitsOnly

@Composable
fun AdminAddDoctorScreen(
    onCreateDoctor: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("") }
    var firstNameError by remember { mutableStateOf<String?>(null) }

    var lastName by remember { mutableStateOf("") }
    var lastNameError by remember { mutableStateOf<String?>(null) }

    var birthDate by remember { mutableStateOf("") }
    var birthDateError by remember { mutableStateOf<String?>(null) }

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    var phone by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }

    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var rate by remember { mutableStateOf("") }
    var rateError by remember { mutableStateOf<String?>(null) }

    var salary by remember { mutableStateOf("") }
    var salaryError by remember { mutableStateOf<String?>(null) }

    var bonus by remember { mutableStateOf("") }
    var bonusError by remember { mutableStateOf<String?>(null) }

    var specialtyId by remember { mutableStateOf("") }
    var specialtyIdError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        cursorColor = Color.Black,
        focusedIndicatorColor = Color(0xFF2196F3),
        unfocusedIndicatorColor = Color.Gray
    )

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

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Información Personal",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Nombre ---
        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
                if (firstNameError != null) firstNameError = validateNamePart(it.trim(), "Nombre")
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            isError = firstNameError != null,
            supportingText = { firstNameError?.let { e -> Text(e, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Apellido ---
        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
                if (lastNameError != null) lastNameError = validateNamePart(it.trim(), "Apellido")
            },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            isError = lastNameError != null,
            supportingText = { lastNameError?.let { e -> Text(e, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Fecha de nacimiento ---
        OutlinedTextField(
            value = birthDate,
            onValueChange = {
                val digits = it.filter(Char::isDigit).take(8)
                val formatted = buildString {
                    for ((i, c) in digits.withIndex()) {
                        append(c)
                        if (i == 3 || i == 5) append('-')
                    }
                }
                birthDate = formatted
                if (birthDateError != null) {
                    birthDateError = when {
                        formatted.isBlank() -> "La fecha es obligatoria"
                        formatted.length == 10 -> validateFechaNacimiento(formatted)
                        else -> null
                    }
                }
            },
            label = { Text("Fecha de nacimiento (DD-MM-YYYY)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            isError = birthDateError != null,
            supportingText = { birthDateError?.let { e -> Text(e, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Correo ---
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (emailError != null) emailError = validateEmail(it.trim())
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            isError = emailError != null,
            supportingText = { emailError?.let { e -> Text(e, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Teléfono ---
        OutlinedTextField(
            value = phone,
            onValueChange = {
                val digits = it.filter(Char::isDigit).take(15)
                phone = digits
                if (phoneError != null) phoneError = validatePhoneDigitsOnly(digits)
            },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            isError = phoneError != null,
            supportingText = { phoneError?.let { e -> Text(e, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Contraseña ---
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = if (it.length < 6) "Mínimo 6 caracteres" else null
            },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            isError = passwordError != null,
            supportingText = { passwordError?.let { e -> Text(e, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Tarifa Consulta ---
        OutlinedTextField(
            value = rate,
            onValueChange = {
                rate = it.filter(Char::isDigit)
                rateError = if (rate.isBlank()) "Campo obligatorio" else null
            },
            label = { Text("Tarifa Consulta") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            isError = rateError != null,
            supportingText = { rateError?.let { e -> Text(e, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Sueldo ---
        OutlinedTextField(
            value = salary,
            onValueChange = {
                salary = it.filter(Char::isDigit)
                salaryError = if (salary.isBlank()) "Campo obligatorio" else null
            },
            label = { Text("Sueldo") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            isError = salaryError != null,
            supportingText = { salaryError?.let { e -> Text(e, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Bono ---
        OutlinedTextField(
            value = bonus,
            onValueChange = {
                bonus = it.filter(Char::isDigit)
                bonusError = if (bonus.isBlank()) "Campo obligatorio" else null
            },
            label = { Text("Bono") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            isError = bonusError != null,
            supportingText = { bonusError?.let { e -> Text(e, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- ID Especialidad ---
        OutlinedTextField(
            value = specialtyId,
            onValueChange = {
                specialtyId = it.filter(Char::isDigit)
                specialtyIdError = if (specialtyId.isBlank()) "Campo obligatorio" else null
            },
            label = { Text("ID Especialidad") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            isError = specialtyIdError != null,
            supportingText = { specialtyIdError?.let { e -> Text(e, color = Color.Red) } },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                firstNameError = validateNamePart(firstName.trim(), "Nombre")
                lastNameError = validateNamePart(lastName.trim(), "Apellido")
                birthDateError = validateFechaNacimiento(birthDate.trim())
                emailError = validateEmail(email.trim())
                phoneError = validatePhoneDigitsOnly(phone.trim())
                passwordError = if (password.length < 6) "Mínimo 6 caracteres" else null
                rateError = if (rate.isBlank()) "Campo obligatorio" else null
                salaryError = if (salary.isBlank()) "Campo obligatorio" else null
                bonusError = if (bonus.isBlank()) "Campo obligatorio" else null
                specialtyIdError = if (specialtyId.isBlank()) "Campo obligatorio" else null

                val hasError = listOf(
                    firstNameError,
                    lastNameError,
                    birthDateError,
                    emailError,
                    phoneError,
                    passwordError,
                    rateError,
                    salaryError,
                    bonusError,
                    specialtyIdError
                ).any { it != null }

                if (hasError) {
                    Toast.makeText(context, "Corrige los errores antes de continuar", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Formulario válido", Toast.LENGTH_SHORT).show()
                    onCreateDoctor()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF27AE60),
                contentColor = Color.White
            )
        ) {
            Text(text = "Agregar Doctor", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
