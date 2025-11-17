package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validateLoginPassword
import com.example.app_clinica_atl.domain.validation.validateRegisterPassword
import com.example.app_clinica_atl.domain.validation.validateRequired
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
    val userRole: String? = null
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
    private val userRepository: UserRepository,
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
        val nameError = validateRequired(name, "Nombre")
        _registerUiState.update { it.copy(firstName = name, firstNameError = nameError, registerError = null) }
    }
    fun onRegisterLastNameChange(lastName: String) {
        val nameError = validateRequired(lastName, "Apellido")
        _registerUiState.update { it.copy(lastName = lastName, lastNameError = nameError, registerError = null) }
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
        _loginUiState.update { it.copy(isLoading = true, loginError = null) }
        viewModelScope.launch {
            val result = userRepository.login(_loginUiState.value.email, _loginUiState.value.password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                userPreferences.saveUserSession(user.id, user.role)
                _loginUiState.update { it.copy(isLoading = false, loginSuccess = true, userRole = user.role) }
            } else {
                _loginUiState.update { it.copy(isLoading = false, loginError = result.exceptionOrNull()?.message ?: "Error desconocido") }
            }
        }
    }
    fun registerUser() {
        // ... (Validación final)
        val s = _registerUiState.value
        val nameError = validateRequired(s.firstName, "Nombre")
        val lastNameError = validateRequired(s.lastName, "Apellido")
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
            val newUser = UserEntity(
                name = "${s.firstName} ${s.lastName}",
                email = s.email, phone = s.phone, password = s.password,
                role = "paciente", specialty = null, salary = null
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

    // --- ¡¡FUNCIÓN DE LOGOUT CORREGIDA!! ---
    fun logout() {
        viewModelScope.launch {
            // 1. Limpia la sesión en DataStore
            userPreferences.clearUserSession()

            // 2. Resetea el estado de ESTE ViewModel a su estado inicial
            // (Esto arregla el bug del "amague")
            _loginUiState.update { LoginUiState() }
            _registerUiState.update { RegisterUiState() }
        }
    }
}