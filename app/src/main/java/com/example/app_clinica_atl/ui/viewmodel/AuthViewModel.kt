package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.data.repository.UserRepository
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
    val userRole: String? = null // <-- ¡¡CAMBIO AÑADIDO!!
)

// --- Estados de UI para Registro ---
data class RegisterUiState(
    val name: String = "",
    val nameError: String? = null,
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

/**
 * ViewModel para Login y Registro.
 */
class AuthViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // --- State para Login ---
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    // --- State para Registro ---
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    val userRoleFlow = userPreferences.userRoleFlow


    // --- Handlers de UI para Login ---
    fun onLoginEmailChange(email: String) {
        _loginUiState.update { it.copy(email = email, emailError = null, loginError = null) }
    }

    fun onLoginPasswordChange(password: String) {
        _loginUiState.update { it.copy(password = password, passwordError = null, loginError = null) }
    }

    // --- Handlers de UI para Registro ---
    fun onRegisterNameChange(name: String) {
        _registerUiState.update { it.copy(name = name, nameError = null, registerError = null) }
    }

    fun onRegisterEmailChange(email: String) {
        _registerUiState.update { it.copy(email = email, emailError = null, registerError = null) }
    }

    fun onRegisterPhoneChange(phone: String) {
        _registerUiState.update { it.copy(phone = phone, phoneError = null, registerError = null) }
    }

    fun onRegisterPasswordChange(password: String) {
        _registerUiState.update { it.copy(password = password, passwordError = null, registerError = null) }
    }

    fun onRegisterConfirmPasswordChange(password: String) {
        _registerUiState.update { it.copy(confirmPassword = password, confirmPasswordError = null, registerError = null) }
    }

    // --- Lógica de Negocio ---

    fun loginUser() {
        _loginUiState.update { it.copy(isLoading = true, loginError = null) }

        val email = _loginUiState.value.email
        val password = _loginUiState.value.password

        val emailError = validateEmail(email)
        val passwordError = validateLoginPassword(password)

        if (emailError != null || passwordError != null) {
            _loginUiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            val result = userRepository.login(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                userPreferences.saveUserSession(user.id, user.role)
                _loginUiState.update {
                    it.copy(
                        isLoading = false,
                        loginSuccess = true,
                        userRole = user.role // <-- ¡¡CAMBIO AÑADIDO!!
                    )
                }
            } else {
                _loginUiState.update {
                    it.copy(
                        isLoading = false,
                        loginError = result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    fun registerUser() {
        _registerUiState.update { it.copy(isLoading = true, registerError = null) }
        val s = _registerUiState.value

        val nameError = validateRequired(s.name, "Nombre")
        val emailError = validateEmail(s.email)
        val phoneError = validateRequired(s.phone, "Teléfono")
        val passwordResult = validateRegisterPassword(s.password, s.confirmPassword)

        if (nameError != null || emailError != null || phoneError != null || passwordResult.isFailure) {
            _registerUiState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    phoneError = phoneError,
                    passwordError = passwordResult.exceptionOrNull()?.message?.takeIf { msg -> "débil" in msg },
                    confirmPasswordError = passwordResult.exceptionOrNull()?.message?.takeIf { msg -> "coinciden" in msg },
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            val newUser = UserEntity(
                name = s.name,
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
                _registerUiState.update {
                    it.copy(
                        isLoading = false,
                        registerError = result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                }
            }
        }
    }
}