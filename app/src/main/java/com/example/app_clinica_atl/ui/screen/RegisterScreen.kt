// Archivo: com.example.app_clinica_atl.ui.screen.RegisterScreen.kt

package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.app_clinica_atl.data.model.RoleOption // <--- CAMBIO: Import ajustado

// --- CAMBIO: Firma de RegisterScreenVm actualizada (Añadir roles) ---
@Composable
fun RegisterScreenVm(
    vm: AuthViewModel,
    onRegisteredNavigateLogin: () -> Unit,
    onGoLogin: () -> Unit
) {

    val state by vm.register.collectAsStateWithLifecycle()
    val roles = vm.availableRoles

    if (state.success) {
        vm.clearRegisterResult()
        onRegisteredNavigateLogin()
    }

    RegisterScreen(
        nombre = state.nombre,
        apellido = state.apellido,
        fecha_nacimiento = state.fecha_nacimiento,
        email = state.email,
        phone = state.phone,
        pass = state.pass,
        confirm = state.confirm,

        nombreError = state.nombreError,
        apellidoError = state.apellidoError,
        fechaNacimientoError = state.fechaNacimientoError,
        emailError = state.emailError,
        phoneError = state.phoneError,
        passError = state.passError,
        confirmError = state.confirmError,

        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,

        // --- NUEVOS PARÁMETROS DE ROL ---
        selectedRole = state.selectedRole,
        availableRoles = roles,

        onNombreChange = vm::onNombreChange,
        onApellidoChange = vm::onApellidoChange,
        onFechaNacimientoChange = vm::onFechaNacimientoChange,
        onEmailChange = vm::onRegisterEmailChange,
        onPhoneChange = vm::onPhoneChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,
        onRoleSelect = vm::onRoleSelect,

        onSubmit = vm::submitRegister,
        onGoLogin = onGoLogin
    )
}


// --- CAMBIO: Firma de RegisterScreen actualizada (Añadir roles) ---
@Composable
private fun RegisterScreen(
    nombre: String,
    apellido: String,
    fecha_nacimiento: String,
    email: String,
    phone: String,
    pass: String,
    confirm: String,
    nombreError: String?,
    apellidoError: String?,
    fechaNacimientoError: String?,
    emailError: String?,
    phoneError: String?,
    passError: String?,
    confirmError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,

    // --- NUEVOS PARÁMETROS DE ROL ---
    selectedRole: RoleOption,
    availableRoles: List<RoleOption>,
    onRoleSelect: (RoleOption) -> Unit,

    onNombreChange: (String) -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Registro",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))

            // ---------- 1. NOMBRE ----------
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre") },
                singleLine = true,
                isError = nombreError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreError != null) {
                Text(nombreError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            // ---------- 2. APELLIDO ----------
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

            // ---------- 3. FECHA DE NACIMIENTO ----------
            OutlinedTextField(
                value = fecha_nacimiento,
                onValueChange = onFechaNacimientoChange,
                label = { Text("Fecha de Nacimiento") },
                placeholder = { Text("DD-MM-YYYY") },
                singleLine = true,
                isError = fechaNacimientoError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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

            // ---------- NUEVO: SELECCIÓN DE ROL ----------
            RoleSelector(
                selectedRole = selectedRole,
                availableRoles = availableRoles,
                onRoleSelect = onRoleSelect
            )
            Spacer(Modifier.height(8.dp))
            // ---------------------------------------------

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


// --- NUEVO COMPOSABLE: Componente de selección de rol (Usa RoleOption importado) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelector(
    selectedRole: RoleOption,
    availableRoles: List<RoleOption>,
    onRoleSelect: (RoleOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedRole.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de Usuario") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableRoles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role.name) },
                    onClick = {
                        onRoleSelect(role)
                        expanded = false
                    }
                )
            }
        }
    }
}