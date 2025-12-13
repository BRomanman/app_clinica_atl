package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions // <-- ¡IMPORT AÑADIDO!
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons // <-- ¡IMPORT AÑADIDO!
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon // <-- ¡IMPORT AÑADIDO!
import androidx.compose.material3.IconButton // <-- ¡IMPORT AÑADIDO!
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // <-- ¡IMPORT AÑADIDO!
import androidx.compose.runtime.saveable.rememberSaveable // <-- ¡IMPORT AÑADIDO!
import androidx.compose.runtime.setValue // <-- ¡IMPORT AÑADIDO!
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType // <-- ¡IMPORT AÑADIDO!
import androidx.compose.ui.text.input.PasswordVisualTransformation // <-- ¡IMPORT AÑADIDO!
import androidx.compose.ui.text.input.VisualTransformation // <-- ¡IMPORT AÑADIDO!
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreenVm(
    registerViewModel: AuthViewModel,
    onRegisterSuccessNavigate: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val uiState by registerViewModel.registerUiState.collectAsState()

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            onRegisterSuccessNavigate()
        }
    }

    RegisterScreen(
        // --- ¡¡CAMPOS ACTUALIZADOS!! ---
        firstName = uiState.firstName,
        onFirstNameChange = registerViewModel::onRegisterFirstNameChange,
        firstNameError = uiState.firstNameError,
        lastName = uiState.lastName,
        onLastNameChange = registerViewModel::onRegisterLastNameChange,
        lastNameError = uiState.lastNameError,
        // --- FIN DE CAMBIOS ---
        email = uiState.email,
        onEmailChange = registerViewModel::onRegisterEmailChange,
        emailError = uiState.emailError,
        phone = uiState.phone,
        onPhoneChange = registerViewModel::onRegisterPhoneChange,
        phoneError = uiState.phoneError,
        password = uiState.password,
        onPasswordChange = registerViewModel::onRegisterPasswordChange,
        passwordError = uiState.passwordError,
        confirmPassword = uiState.confirmPassword,
        onConfirmPasswordChange = registerViewModel::onRegisterConfirmPasswordChange,
        confirmPasswordError = uiState.confirmPasswordError,
        onRegisterClick = registerViewModel::registerUser,
        isLoading = uiState.isLoading,
        registerError = uiState.registerError,
        onBackToLogin = onBackToLogin
    )
}

@Composable
fun RegisterScreen(
    // --- ¡¡FIRMA ACTUALIZADA!! ---
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    firstNameError: String?,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    lastNameError: String?,
    // --- FIN DE CAMBIOS ---
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    phone: String,
    onPhoneChange: (String) -> Unit,
    phoneError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordError: String?,
    onRegisterClick: () -> Unit,
    isLoading: Boolean,
    registerError: String?,
    onBackToLogin: () -> Unit
) {
    // --- ¡¡ESTADOS AÑADIDOS PARA VISIBILIDAD!! ---
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_clean),
                contentDescription = "Logo de la Clínica",
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 16.dp)
            )
            Text(
                "Crea tu cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- ¡¡CAMPO "NOMBRE" ACTUALIZADO A DOS CAMPOS!! ---
            OutlinedTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = firstNameError != null,
                singleLine = true
            )
            firstNameError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = lastNameError != null,
                singleLine = true
            )
            lastNameError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            // --- FIN DE CAMBIOS ---
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Email
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = emailError != null,
                singleLine = true
            )
            emailError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- ¡¡CAMPO "TELÉFONO" ACTUALIZADO!! ---
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Teléfono (+56912345678)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = phoneError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone) // <-- Teclado
            )
            phoneError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- ¡¡CAMPO "CONTRASEÑA" ACTUALIZADO!! ---
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = passwordError != null,
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }
            )
            passwordError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- ¡¡CAMPO "CONFIRMAR CONTRASEÑA" ACTUALIZADO!! ---
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = confirmPasswordError != null,
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }
            )
            confirmPasswordError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Registro
            Button(
                onClick = onRegisterClick,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Registrarse", color = Color.White, fontSize = 16.sp)
                }
            }

            // Error de Registro
            registerError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onBackToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                TODO("agregar algo colorido para que se vea")
                Text("Volver a iniciar sesion")
            }
        }
    }
}
