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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// NO MÁS HILT
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreenVm(
    authViewModel: AuthViewModel, // <-- ¡¡AQUÍ ESTÁ EL CAMBIO!! (Quitamos = hiltViewModel())
    onRegisterSuccessNavigate: () -> Unit
) {
    val uiState by authViewModel.registerUiState.collectAsState()

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            onRegisterSuccessNavigate()
        }
    }

    RegisterScreen(
        name = uiState.name,
        onNameChange = authViewModel::onRegisterNameChange,
        nameError = uiState.nameError,
        email = uiState.email,
        onEmailChange = authViewModel::onRegisterEmailChange,
        emailError = uiState.emailError,
        phone = uiState.phone,
        onPhoneChange = authViewModel::onRegisterPhoneChange,
        phoneError = uiState.phoneError,
        password = uiState.password,
        onPasswordChange = authViewModel::onRegisterPasswordChange,
        passwordError = uiState.passwordError,
        confirmPassword = uiState.confirmPassword,
        onConfirmPasswordChange = authViewModel::onRegisterConfirmPasswordChange,
        confirmPasswordError = uiState.confirmPasswordError,
        onRegisterClick = authViewModel::registerUser,
        isLoading = uiState.isLoading,
        registerError = uiState.registerError
    )
}

@Composable
fun RegisterScreen(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?,
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
    registerError: String?
) {
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

            // Campo de Nombre
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = nameError != null,
                singleLine = true
            )
            nameError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
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

            // Campo de Teléfono
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = phoneError != null,
                singleLine = true
            )
            phoneError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = passwordError != null,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            passwordError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Confirmar Contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = confirmPasswordError != null,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
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
        }
    }
}