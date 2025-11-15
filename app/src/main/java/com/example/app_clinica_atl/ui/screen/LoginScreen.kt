package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreenVm(
    authViewModel: AuthViewModel,
    onLoginSuccessNavigate: (role: String) -> Unit, // <-- ¡¡CAMBIO!!
    onGoRegister: () -> Unit
) {
    val uiState by authViewModel.loginUiState.collectAsState()
    val (checkedState, onStateChange) = rememberSaveable { mutableStateOf(false) }

    // --- ¡¡CAMBIO!! ---
    // Ahora reacciona al rol y lo pasa a la navegación
    LaunchedEffect(uiState.loginSuccess, uiState.userRole) {
        if (uiState.loginSuccess && uiState.userRole != null) {
            onLoginSuccessNavigate(uiState.userRole!!)
        }
    }
    // --- FIN DEL CAMBIO ---

    LoginScreen(
        email = uiState.email,
        onEmailChange = authViewModel::onLoginEmailChange,
        emailError = uiState.emailError,
        password = uiState.password,
        onPasswordChange = authViewModel::onLoginPasswordChange,
        passwordError = uiState.passwordError,
        checkedState = checkedState,
        onCheckedChange = onStateChange,
        onLoginClick = authViewModel::loginUser,
        onGoRegisterClick = onGoRegister,
        isLoading = uiState.isLoading,
        loginError = uiState.loginError
    )
}

@Composable
fun LoginScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    checkedState: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onGoRegisterClick: () -> Unit,
    isLoading: Boolean,
    loginError: String?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_clean),
                contentDescription = "Logo de la Clínica",
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "¡Bienvenido de vuelta!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Inicia sesión para continuar",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checkedState,
                    onCheckedChange = onCheckedChange
                )
                Text("Recuérdame")
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "¿Olvidaste tu contraseña?",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLoginClick,
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
                    Text("Iniciar Sesión", color = Color.White, fontSize = 16.sp)
                }
            }

            loginError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text("¿No tienes una cuenta? ")
                Text(
                    "Regístrate",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onGoRegisterClick() }
                )
            }
        }
    }
}