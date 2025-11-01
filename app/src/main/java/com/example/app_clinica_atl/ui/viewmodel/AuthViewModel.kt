package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.domain.validation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ... (Data classes LoginUiState y RegisterUiState se mantienen igual) ...

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,
    val loggedUser: UserEntity? = null
)

data class RegisterUiState(
    val nombre: String = "",
    val apellido: String = "",
    val fecha_nacimiento: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirm: String = "",

    val nombreError: String? = null,
    val apellidoError: String? = null,
    val fechaNacimientoError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)


class AuthViewModel(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _userDisplayName = MutableStateFlow("Bienvenido/a a la Clínica")
    val userDisplayName: StateFlow<String> = _userDisplayName


    // ----------------- LOGIN: Handlers -----------------
    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }
    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.emailError == null && s.email.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value

        val noErrors = listOf(
            s.nombreError, s.apellidoError, s.fechaNacimientoError, s.emailError,
            s.phoneError, s.passError, s.confirmError
        ).all { it == null }

        val filled = s.nombre.isNotBlank() && s.apellido.isNotBlank() &&
                s.fecha_nacimiento.isNotBlank() && s.email.isNotBlank() &&
                s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()

        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)
            val result = repository.login(s.email.trim(), s.pass)
            val user = result.getOrNull()
            val errorMessage = result.exceptionOrNull()?.message ?: "Error de autenticación"
            _login.update {
                if (user != null) {
                    userPreferences.setLoggedIn(true)
                    _userDisplayName.value = "Hola, ${user.nombre} (${if(user.id_rol == 1L) "Paciente" else if(user.id_rol == 2L) "Doctor" else "Admin"})."

                    it.copy(
                        isSubmitting = false,
                        success = true,
                        errorMsg = null,
                        loggedUser = user
                    )
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = errorMessage,
                        loggedUser = null
                    )
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null, loggedUser = null) }
    }

    // *** CAMBIO CRÍTICO: Función suspendida para esperar el DataStore ***
    suspend fun logout() {
        // 1. Limpiar estado local
        _login.value = LoginUiState()
        _register.value = RegisterUiState()
        _userDisplayName.value = "Bienvenido/a a la Clínica"

        // 2. Limpiar DataStore (EJECUCIÓN DIRECTA, NO EN UN NUEVO LAUNCH)
        userPreferences.setLoggedIn(false) // <-- ESTO AHORA ES SINCRÓNICO CON LA LLAMADA EXTERNA
    }
    // *************************************

    // ... (el resto de funciones de Registro se mantienen) ...
    fun onNombreChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(nombre = filtered, nombreError = validateNamePart(filtered, "El nombre"))
        }
        recomputeRegisterCanSubmit()
    }
    fun onApellidoChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(apellido = filtered, apellidoError = validateNamePart(filtered, "El apellido"))
        }
        recomputeRegisterCanSubmit()
    }
    fun onFechaNacimientoChange(value: String) {
        _register.update {
            it.copy(fecha_nacimiento = value, fechaNacimientoError = validateFechaNacimiento(value))
        }
        recomputeRegisterCanSubmit()
    }
    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }
    fun onPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _register.update {
            it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly))
        }
        recomputeRegisterCanSubmit()
    }
    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }
    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) }
        recomputeRegisterCanSubmit()
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(700)

            val result = repository.register(
                nombre = s.nombre.trim(),
                apellido = s.apellido.trim(),
                fecha_nacimiento = s.fecha_nacimiento.trim(),
                email = s.email.trim(),
                phone = s.phone.trim(),
                password = s.pass
            )

            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar")
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }
}