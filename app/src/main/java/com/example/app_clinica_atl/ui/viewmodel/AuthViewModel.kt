package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.domain.validation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.app_clinica_atl.data.model.RoleOption // <--- CAMBIO: Nuevo Import desde data.model

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

// --- CAMBIO: RegisterUiState actualizado (Usa RoleOption importado) ---
data class RegisterUiState(
    val nombre: String = "",
    val apellido: String = "",
    val fecha_nacimiento: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirm: String = "",

    val selectedRole: RoleOption = RoleOption(1L, "Paciente"),

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
// --- FIN DE CAMBIO ---


class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    // --- CAMBIO: Usa RoleOption importado ---
    val availableRoles: List<RoleOption> = listOf(
        RoleOption(id = 1L, name = "Paciente"),
        RoleOption(id = 2L, name = "Doctor")
    )
    // ... (rest of the file remains the same logic)
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

    fun logout() {
        _login.value = LoginUiState()
        _register.value = RegisterUiState()
    }

    // ----------------- REGISTRO: handlers y envío -----------------

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

    // --- CAMBIO: Usa RoleOption importado ---
    fun onRoleSelect(role: RoleOption) {
        _register.update { it.copy(selectedRole = role) }
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
                password = s.pass,
                id_rol = s.selectedRole.id
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