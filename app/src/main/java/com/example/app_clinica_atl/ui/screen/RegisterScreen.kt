package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <-- CAMBIO: Importar
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll // <-- CAMBIO: Importar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel

// --- CAMBIO: Firma de RegisterScreenVm actualizada ---
@Composable
fun RegisterScreenVm(
    vm: AuthViewModel,
    onRegisteredNavigateLogin: () -> Unit,
    onGoLogin: () -> Unit
) {

    val state by vm.register.collectAsStateWithLifecycle()

    if (state.success) {
        vm.clearRegisterResult()
        onRegisteredNavigateLogin()
    }

    RegisterScreen(
        nombre = state.nombre,               // 1) Renombrado
        apellido = state.apellido,           // 2) Nuevo
        fecha_nacimiento = state.fecha_nacimiento, // 3) Nuevo
        email = state.email,                 // 4)
        phone = state.phone,                 // 5)
        pass = state.pass,                   // 6)
        confirm = state.confirm,             // 7)

        nombreError = state.nombreError,     // Errores
        apellidoError = state.apellidoError,
        fechaNacimientoError = state.fechaNacimientoError,
        emailError = state.emailError,
        phoneError = state.phoneError,
        passError = state.passError,
        confirmError = state.confirmError,

        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,

        onNombreChange = vm::onNombreChange, // Handlers
        onApellidoChange = vm::onApellidoChange,
        onFechaNacimientoChange = vm::onFechaNacimientoChange,
        onEmailChange = vm::onRegisterEmailChange,
        onPhoneChange = vm::onPhoneChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,

        onSubmit = vm::submitRegister,
        onGoLogin = onGoLogin
    )
}


// --- CAMBIO: Firma de RegisterScreen actualizada ---
@Composable
private fun RegisterScreen(
    nombre: String,                 // 1) Renombrado
    apellido: String,               // 2) Nuevo
    fecha_nacimiento: String,       // 3) Nuevo
    email: String,                  // 4)
    phone: String,                  // 5)
    pass: String,                   // 6)
    confirm: String,                // 7)
    nombreError: String?,           // Errores
    apellidoError: String?,
    fechaNacimientoError: String?,
    emailError: String?,
    phoneError: String?,
    passError: String?,
    confirmError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onNombreChange: (String) -> Unit, // Handlers
    onApellidoChange: (String) -> Unit,
    onFechaNacimientoChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoLogin: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.primaryContainer
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- CAMBIO: Añadido .verticalScroll() para evitar que el teclado tape los campos ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // <-- AÑADIDO
        ) {
            Text(
                text = "Registro",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))

            // ---------- 1. NOMBRE ----------
            OutlinedTextField(
                value = nombre, // <-- CAMBIO
                onValueChange = onNombreChange, // <-- CAMBIO
                label = { Text("Nombre") },
                singleLine = true,
                isError = nombreError != null, // <-- CAMBIO
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreError != null) { // <-- CAMBIO
                Text(nombreError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            // ---------- 2. APELLIDO (NUEVO) ----------
            OutlinedTextField(
                value = apellido,
                onValueChange = onApellidoChange,
                label = { Text("Apellido") },
                singleLine = true,
                isError = apellidoError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )
            if (apellidoError != null) {
                Text(apellidoError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            // ---------- 3. FECHA DE NACIMIENTO (NUEVO) ----------
            OutlinedTextField(
                value = fecha_nacimiento,
                onValueChange = onFechaNacimientoChange,
                label = { Text("Fecha de Nacimiento") },
                placeholder = { Text("YYYY-MM-DD") }, // Placeholder para guiar
                singleLine = true,
                isError = fechaNacimientoError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), // Usar Text para los guiones
                modifier = Modifier.fillMaxWidth()
            )
            if (fechaNacimientoError != null) {
                Text(fechaNacimientoError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            // ---------- 4. EMAIL ----------
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null) {
                Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            // ---------- 5. TELÉFONO ----------
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Teléfono") },
                singleLine = true,
                isError = phoneError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            if (phoneError != null) {
                Text(phoneError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            // ---------- 6. PASSWORD ----------
            OutlinedTextField(
                value = pass,
                onValueChange = onPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                isError = passError != null,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (passError != null) {
                Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            // ---------- 7. CONFIRMAR PASSWORD ----------
            OutlinedTextField(
                value = confirm,
                onValueChange = onConfirmChange,
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                isError = confirmError != null,
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showConfirm) "Ocultar confirmación" else "Mostrar confirmación"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (confirmError != null) {
                Text(confirmError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))

            // ---------- BOTÓN REGISTRAR ----------
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Creando cuenta...")
                } else {
                    Text("Registrar")
                }
            }

            if (errorMsg != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            // ---------- BOTÓN IR A LOGIN ----------
            OutlinedButton(onClick = onGoLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Login")
            }
        }
    }
}