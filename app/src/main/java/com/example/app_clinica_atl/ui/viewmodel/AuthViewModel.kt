package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber // <-- ¡IMPORT AÑADIDO!
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validateLoginPassword
import com.example.app_clinica_atl.domain.validation.validateRegisterPassword
import com.example.app_clinica_atl.domain.validation.validatePersonName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- Estados de UI para Login ---
data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccess: Boolean = false,
    val userRole: String? = null,
    val weakPasswordWarning: String? = null,
    val isResetDialogOpen: Boolean = false,
    val resetEmail: String = "",
    val resetEmailError: String? = null,
    val resetError: String? = null,
    val isSendingReset: Boolean = false,
    val resetSuccessMessage: String? = null
)

// --- Estados de UI para Registro ---
data class RegisterUiState(
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val registerError: String? = null,
    val registerSuccess: Boolean = false
)

class AuthViewModel(
    private val userRepository: UsuariosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    val userRoleFlow = userPreferences.userRoleFlow

    // --- Handlers de Login (con validación en tiempo real) ---
    fun onLoginEmailChange(email: String) {
        val emailError = validateEmail(email)
        _loginUiState.update { it.copy(email = email, emailError = emailError, loginError = null) }
    }
    fun onLoginPasswordChange(password: String) {
        val passwordError = validateLoginPassword(password)
        _loginUiState.update { it.copy(password = password, passwordError = passwordError, loginError = null) }
    }

    // --- Handlers de Registro (con validación en tiempo real) ---
    fun onRegisterFirstNameChange(name: String) {
        val sanitized = sanitizePersonNameInput(name)
        val nameError = validatePersonName(sanitized, "Nombre")
        _registerUiState.update { it.copy(firstName = sanitized, firstNameError = nameError, registerError = null) }
    }
    fun onRegisterLastNameChange(lastName: String) {
        val sanitized = sanitizePersonNameInput(lastName)
        val nameError = validatePersonName(sanitized, "Apellido")
        _registerUiState.update { it.copy(lastName = sanitized, lastNameError = nameError, registerError = null) }
    }
    fun onRegisterEmailChange(email: String) {
        val emailError = validateEmail(email)
        _registerUiState.update { it.copy(email = email, emailError = emailError, registerError = null) }
    }
    fun onRegisterPhoneChange(phone: String) {
        val phoneError = validateChileanPhoneNumber(phone)
        _registerUiState.update { it.copy(phone = phone, phoneError = phoneError, registerError = null) }
    }
    fun onRegisterPasswordChange(password: String) {
        val passwordResult = validateRegisterPassword(password, _registerUiState.value.confirmPassword)
        _registerUiState.update {
            it.copy(
                password = password,
                passwordError = passwordResult.exceptionOrNull()?.message?.takeIf { msg -> "débil" in msg },
                confirmPasswordError = passwordResult.exceptionOrNull()?.message?.takeIf { msg -> "coinciden" in msg },
                registerError = null
            )
        }
    }
    fun onRegisterConfirmPasswordChange(password: String) {
        val passwordResult = validateRegisterPassword(_registerUiState.value.password, password)
        _registerUiState.update {
            it.copy(
                confirmPassword = password,
                passwordError = passwordResult.exceptionOrNull()?.message?.takeIf { msg -> "débil" in msg },
                confirmPasswordError = passwordResult.exceptionOrNull()?.message?.takeIf { msg -> "coinciden" in msg },
                registerError = null
            )
        }
    }

    // --- Lógica de Negocio (sin cambios) ---
    fun loginUser() {
        // ... (Validación final)
        val emailError = validateEmail(_loginUiState.value.email)
        val passwordError = validateLoginPassword(_loginUiState.value.password)
        if (emailError != null || passwordError != null) {
            _loginUiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }
        _loginUiState.update { it.copy(isLoading = true, loginError = null, weakPasswordWarning = null) }
        viewModelScope.launch {
            val result = userRepository.login(_loginUiState.value.email, _loginUiState.value.password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                userPreferences.saveUserSession(user.id, user.role)
                val weakPass = isWeakDoctorPassword(user.role, user.name, _loginUiState.value.password)
                _loginUiState.update {
                    it.copy(
                        isLoading = false,
                        loginSuccess = true,
                        userRole = user.role,
                        weakPasswordWarning = if (weakPass) "Tu contraseña es débil (contiene tu nombre y año). Cámbiala cuanto antes." else null
                    )
                }
            } else {
                _loginUiState.update { it.copy(isLoading = false, loginError = result.exceptionOrNull()?.message ?: "Error desconocido") }
            }
        }
    }

    fun openResetDialog() {
        _loginUiState.update {
            it.copy(
                isResetDialogOpen = true,
                resetEmail = it.email,
                resetEmailError = null,
                resetError = null,
                resetSuccessMessage = null,
                loginError = null
            )
        }
    }

    fun closeResetDialog() {
        _loginUiState.update {
            it.copy(
                isResetDialogOpen = false,
                isSendingReset = false,
                resetError = null,
                resetEmailError = null
            )
        }
    }

    fun onResetEmailChange(email: String) {
        val emailError = validateEmail(email)
        _loginUiState.update {
            it.copy(
                resetEmail = email,
                resetEmailError = emailError,
                resetError = null
            )
        }
    }

    fun sendResetInstructions() {
        val current = _loginUiState.value
        val targetEmail = current.resetEmail.ifBlank { current.email }
        val emailError = validateEmail(targetEmail)

        if (emailError != null) {
            _loginUiState.update { it.copy(resetEmail = targetEmail, resetEmailError = emailError) }
            return
        }

        viewModelScope.launch {
            _loginUiState.update {
                it.copy(
                    isSendingReset = true,
                    resetEmail = targetEmail,
                    resetEmailError = null,
                    resetError = null,
                    resetSuccessMessage = null
                )
            }

            val result = userRepository.requestPasswordReset(targetEmail)

            _loginUiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isSendingReset = false,
                        isResetDialogOpen = false,
                        resetSuccessMessage = result.getOrNull(),
                        resetError = null
                    )
                } else {
                    it.copy(
                        isSendingReset = false,
                        resetError = result.exceptionOrNull()?.message
                            ?: "No se pudo enviar el correo de recuperación."
                    )
                }
            }
        }
    }

    fun clearResetMessage() {
        _loginUiState.update { it.copy(resetSuccessMessage = null) }
    }
    fun registerUser() {
        // ... (Validación final)
        val s = _registerUiState.value
        val nameError = validatePersonName(s.firstName, "Nombre")
        val lastNameError = validatePersonName(s.lastName, "Apellido")
        val emailError = validateEmail(s.email)
        val phoneError = validateChileanPhoneNumber(s.phone)
        val passwordResult = validateRegisterPassword(s.password, s.confirmPassword)
        if (nameError != null || lastNameError != null || emailError != null || phoneError != null || passwordResult.isFailure) {
            _registerUiState.update {
                it.copy(
                    firstNameError = nameError, lastNameError = lastNameError,
                    emailError = emailError, phoneError = phoneError,
                    passwordError = passwordResult.exceptionOrNull()?.message?.takeIf { msg -> "débil" in msg },
                    confirmPasswordError = passwordResult.exceptionOrNull()?.message?.takeIf { msg -> "coinciden" in msg },
                    isLoading = false
                )
            }
            return
        }
        _registerUiState.update { it.copy(isLoading = true, registerError = null) }
        viewModelScope.launch {
            // ¡CAMBIO! Combinamos firstName y lastName en el 'name' de la entidad
            val newUser = UsuarioDto(
                name = "${s.firstName} ${s.lastName}",
                email = s.email,
                phone = s.phone,
                password = s.password,
                role = "paciente",
                specialty = null,
                salary = null
            )
            val result = userRepository.register(newUser)
            if (result.isSuccess) {
                val registeredUser = result.getOrNull()!!
                userPreferences.saveUserSession(registeredUser.id, registeredUser.role)
                _registerUiState.update { it.copy(isLoading = false, registerSuccess = true) }
            } else {
                _registerUiState.update { it.copy(isLoading = false, registerError = result.exceptionOrNull()?.message ?: "Error desconocido") }
            }
        }
    }

    suspend fun logout() {
        // 1. Limpia la sesión en DataStore
        userPreferences.clearUserSession()

        // 2. Resetea el estado de ESTE ViewModel a su estado inicial
        _loginUiState.update { LoginUiState() }
        _registerUiState.update { RegisterUiState() }
    }

    private fun isWeakDoctorPassword(role: String, fullName: String, password: String): Boolean {
        if (!role.equals("doctor", true)) return false
        val normalizedPass = password.lowercase()
        val tokens = fullName.lowercase().split(" ").filter { it.length >= 3 }
        val hasName = tokens.any { normalizedPass.contains(it) }
        val hasYear = Regex("\\d{4}").containsMatchIn(password)
        return hasName && hasYear
    }

    private fun sanitizePersonNameInput(input: String): String =
        input.filter { it.isLetter() || it.isWhitespace() }
}
