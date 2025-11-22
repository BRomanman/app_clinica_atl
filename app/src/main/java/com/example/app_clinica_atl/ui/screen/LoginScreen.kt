package com.example.app_clinica_atl.ui.screen

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions // <-- ¡IMPORT AÑADIDO!
import androidx.compose.material.icons.Icons // <-- ¡IMPORT AÑADIDO!
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType // <-- ¡IMPORT AÑADIDO!
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation // <-- ¡IMPORT AÑADIDO!
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.data.remote.dto.normalizeRole
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreenVm(
    authViewModel: AuthViewModel,
    onLoginSuccessNavigate: (role: String) -> Unit,
    onGoRegister: () -> Unit
) {
    val uiState by authViewModel.loginUiState.collectAsState()
    val storedRole by authViewModel.userRoleFlow.collectAsState(initial = null)
    val context = LocalContext.current

    LaunchedEffect(uiState.loginSuccess, uiState.userRole) {
        if (uiState.loginSuccess && uiState.userRole != null) {
            onLoginSuccessNavigate(uiState.userRole!!)
        }
    }
    LaunchedEffect(storedRole) {
        val role = storedRole?.let { normalizeRole(it) }
        if (role != null && !uiState.loginSuccess) {
            onLoginSuccessNavigate(role)
        }
    }
    LaunchedEffect(uiState.weakPasswordWarning) {
        uiState.weakPasswordWarning?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
    LaunchedEffect(uiState.resetSuccessMessage) {
        uiState.resetSuccessMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            authViewModel.clearResetMessage()
        }
    }

    LoginScreen(
        email = uiState.email,
        onEmailChange = authViewModel::onLoginEmailChange,
        emailError = uiState.emailError,
        password = uiState.password,
        onPasswordChange = authViewModel::onLoginPasswordChange,
        passwordError = uiState.passwordError,
        onLoginClick = authViewModel::loginUser,
        onGoRegisterClick = onGoRegister,
        onForgotPasswordClick = authViewModel::openResetDialog,
        isResetDialogOpen = uiState.isResetDialogOpen,
        resetEmail = uiState.resetEmail.ifBlank { uiState.email },
        resetEmailError = uiState.resetEmailError,
        resetError = uiState.resetError,
        onResetEmailChange = authViewModel::onResetEmailChange,
        onSendReset = authViewModel::sendResetInstructions,
        onDismissReset = authViewModel::closeResetDialog,
        isSendingReset = uiState.isSendingReset,
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
    onLoginClick: () -> Unit,
    onGoRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    isResetDialogOpen: Boolean,
    resetEmail: String,
    resetEmailError: String?,
    resetError: String?,
    onResetEmailChange: (String) -> Unit,
    onSendReset: () -> Unit,
    onDismissReset: () -> Unit,
    isSendingReset: Boolean,
    isLoading: Boolean,
    loginError: String?
) {
    // --- ¡ESTADO AÑADIDO PARA VISIBILIDAD DE CONTRASEÑA! ---
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    if (isResetDialogOpen) {
        ForgotPasswordDialog(
            email = resetEmail,
            emailError = resetEmailError,
            resetError = resetError,
            isSending = isSendingReset,
            onEmailChange = onResetEmailChange,
            onSend = onSendReset,
            onDismiss = onDismissReset
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
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

            // --- ¡¡CAMPO DE EMAIL ACTUALIZADO!! ---
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = emailError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email) // <-- Teclado con @
            )
            emailError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ¡¡CAMPO DE CONTRASEÑA ACTUALIZADO!! ---
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = passwordError != null,
                singleLine = true,
                // Lógica de visibilidad
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


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "¿Olvidaste tu contraseña?",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onForgotPasswordClick),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Login
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

            // Link a Registro
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

@Composable
private fun ForgotPasswordDialog(
    email: String,
    emailError: String?,
    resetError: String?,
    isSending: Boolean,
    onEmailChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recupera tu contraseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Ingresa tu correo y te enviaremos una clave temporal para que vuelvas a entrar y la cambies.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Correo registrado") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError != null
                )
                emailError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                resetError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSend, enabled = !isSending) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isSending) "Enviando..." else "Enviar instrucciones")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
