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


data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccess: Boolean = false,
    val userRole: String? = null,
    val userDoctorId: Long? = null,
    val weakPasswordWarning: String? = null,
    val isResetDialogOpen: Boolean = false,
    val resetEmail: String = "",
    val resetEmailError: String? = null,
    val resetError: String? = null,
    val isSendingReset: Boolean = false,
    val resetSuccessMessage: String? = null,
    val recoveryEmail: String = "",
    val recoveryEmailError: String? = null,
    val recoveryBirthDate: String = "",
    val recoveryBirthDateError: String? = null,
    val recoveryVerificationError: String? = null,
    val isVerifyingRecovery: Boolean = false,
    val recoveryIdentityPassed: Boolean = false,
    val recoveryUserId: Long? = null,
    val recoveryPassword: String = "",
    val recoveryConfirmPassword: String = "",
    val recoveryPasswordError: String? = null,
    val recoveryConfirmPasswordError: String? = null,
    val isUpdatingRecoveryPassword: Boolean = false,
    val recoverySuccessMessage: String? = null
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


// actualiza los estados actuales para que los errores salgan en el momento



class AuthViewModel(
    private val userRepository: UsuariosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    val userRoleFlow = userPreferences.userRoleFlow
    val userDoctorIdFlow = userPreferences.userDoctorIdFlow

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
                val weakPass = isWeakDoctorPassword(user.role, user.name, _loginUiState.value.password)
                _loginUiState.update {
                    it.copy(
                        isLoading = false,
                        loginSuccess = true,
                        userRole = user.role,
                        userDoctorId = user.doctorId,
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

    // --- Recuperación con verificación de datos personales ---
    fun onRecoveryEmailChange(email: String) {
        val emailError = validateEmail(email)
        _loginUiState.update {
            it.copy(
                recoveryEmail = email,
                recoveryEmailError = emailError,
                recoveryVerificationError = null,
                recoveryIdentityPassed = false
            )
        }
    }

    fun onRecoveryBirthDateChange(birthDate: String) {
        val formatted = formatBirthDateInput(birthDate)
        val birthError = validateBirthDateInput(formatted)
        _loginUiState.update {
            it.copy(
                recoveryBirthDate = formatted,
                recoveryBirthDateError = birthError,
                recoveryVerificationError = null,
                recoveryIdentityPassed = false
            )
        }
    }

    fun verifyRecoveryIdentity() {
        val current = _loginUiState.value
        val email = current.recoveryEmail.ifBlank { current.email }
        val emailError = validateEmail(email)
        val birthError = validateBirthDateInput(current.recoveryBirthDate)
        if (emailError != null || birthError != null) {
            _loginUiState.update {
                it.copy(
                    recoveryEmail = email,
                    recoveryEmailError = emailError,
                    recoveryBirthDateError = birthError
                )
            }
            return
        }
        _loginUiState.update {
            it.copy(
                recoveryEmail = email,
                isVerifyingRecovery = true,
                recoveryVerificationError = null,
                recoveryEmailError = null,
                recoveryBirthDateError = null
            )
        }
        viewModelScope.launch {
            val result = userRepository.verifyUserIdentity(email, current.recoveryBirthDate)
            _loginUiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isVerifyingRecovery = false,
                        recoveryIdentityPassed = true,
                        recoveryUserId = result.getOrNull(),
                        recoveryVerificationError = null
                    )
                } else {
                    it.copy(
                        isVerifyingRecovery = false,
                        recoveryIdentityPassed = false,
                        recoveryUserId = null,
                        recoveryVerificationError = result.exceptionOrNull()?.message
                            ?: "No pudimos verificar tus datos."
                    )
                }
            }
        }
    }

    fun consumeRecoveryIdentityFlag() {
        _loginUiState.update { it.copy(recoveryIdentityPassed = false) }
    }

    fun clearRecoverySuccessMessage() {
        _loginUiState.update { it.copy(recoverySuccessMessage = null) }
    }

    fun onRecoveryPasswordChange(password: String) {
        val validation = validateRegisterPassword(password, _loginUiState.value.recoveryConfirmPassword)
        _loginUiState.update {
            it.copy(
                recoveryPassword = password,
                recoveryPasswordError = validation.exceptionOrNull()?.message?.takeIf { msg -> "débil" in msg },
                recoveryConfirmPasswordError = validation.exceptionOrNull()?.message?.takeIf { msg -> "coinciden" in msg },
                recoverySuccessMessage = null
            )
        }
    }

    fun onRecoveryConfirmPasswordChange(password: String) {
        val validation = validateRegisterPassword(_loginUiState.value.recoveryPassword, password)
        _loginUiState.update {
            it.copy(
                recoveryConfirmPassword = password,
                recoveryPasswordError = validation.exceptionOrNull()?.message?.takeIf { msg -> "débil" in msg },
                recoveryConfirmPasswordError = validation.exceptionOrNull()?.message?.takeIf { msg -> "coinciden" in msg },
                recoverySuccessMessage = null
            )
        }
    }

    fun updateRecoveredPassword() {
        val current = _loginUiState.value
        val validation = validateRegisterPassword(current.recoveryPassword, current.recoveryConfirmPassword)
        if (validation.isFailure) {
            _loginUiState.update {
                it.copy(
                    recoveryPasswordError = validation.exceptionOrNull()?.message?.takeIf { msg -> "débil" in msg },
                    recoveryConfirmPasswordError = validation.exceptionOrNull()?.message?.takeIf { msg -> "coinciden" in msg }
                )
            }
            return
        }
        val targetUserId = current.recoveryUserId
        if (targetUserId == null) {
            _loginUiState.update {
                it.copy(recoveryVerificationError = "Debes verificar tus datos antes de cambiar la contraseña.")
            }
            return
        }
        _loginUiState.update { it.copy(isUpdatingRecoveryPassword = true, recoveryVerificationError = null) }
        viewModelScope.launch {
            val result = userRepository.updatePassword(targetUserId, current.recoveryPassword)
            _loginUiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isUpdatingRecoveryPassword = false,
                        recoverySuccessMessage = "Contraseña actualizada. Inicia sesión con tu nueva clave.",
                        recoveryPassword = "",
                        recoveryConfirmPassword = "",
                        recoveryPasswordError = null,
                        recoveryConfirmPasswordError = null
                    )
                } else {
                    it.copy(
                        isUpdatingRecoveryPassword = false,
                        recoveryVerificationError = result.exceptionOrNull()?.message ?: "No se pudo actualizar la contraseña."
                    )
                }
            }
        }
    }

    private fun validateBirthDateInput(birthDate: String): String? {
        if (birthDate.isBlank()) return "Ingresa tu fecha de nacimiento."
        val regex = Regex("^\\d{2}-\\d{2}-\\d{4}\$")
        return if (regex.matches(birthDate.trim())) null else "Usa el formato DD-MM-AAAA."
    }

    private fun formatBirthDateInput(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(8)
        val builder = StringBuilder()
        digits.forEachIndexed { index, c ->
            builder.append(c)
            if (index == 1 || index == 3) builder.append("-")
        }
        return builder.toString()
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
                val parts = registeredUser.name.trim().split(" ", limit = 2)
                val nombre = parts.getOrElse(0) { registeredUser.name }
                val apellido = parts.getOrElse(1) { "" }
                userPreferences.saveUserSession(
                    registeredUser.id,
                    registeredUser.role,
                    registeredUser.doctorId,
                    nombre = nombre,
                    apellido = apellido,
                    correo = registeredUser.email,
                    token = null
                )
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
