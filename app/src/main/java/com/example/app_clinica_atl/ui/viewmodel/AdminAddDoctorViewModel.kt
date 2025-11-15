package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.specialty.SpecialtyEntity
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.data.repository.SpecialtyRepository
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validateRegisterPassword
import com.example.app_clinica_atl.domain.validation.validateRequired
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de añadir doctor.
 */
data class AdminAddDoctorUiState(
    // Datos del formulario
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val salary: String = "",
    val selectedSpecialty: SpecialtyEntity? = null,

    // Lista para el combo box
    val specialties: List<SpecialtyEntity> = emptyList(),

    // Errores de validación
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val salaryError: String? = null,
    val specialtyError: String? = null,

    // Estado general
    val isLoading: Boolean = true, // Empieza en true para cargar especialidades
    val registrationSuccess: Boolean = false,
    val errorMsg: String? = null
)

class AdminAddDoctorViewModel(
    private val userRepository: UserRepository,
    private val specialtyRepository: SpecialtyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAddDoctorUiState())
    val uiState: StateFlow<AdminAddDoctorUiState> = _uiState.asStateFlow()

    init {
        // Carga la lista de especialidades para el combo box
        viewModelScope.launch {
            specialtyRepository.getAllSpecialties()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMsg = e.message) }
                }
                .collect { specialtyList ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            specialties = specialtyList
                        )
                    }
                }
        }
    }

    // --- Handlers de UI ---
    fun onNameChange(name: String) { _uiState.update { it.copy(name = name, nameError = null) } }
    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email, emailError = null) } }
    fun onPhoneChange(phone: String) { _uiState.update { it.copy(phone = phone, phoneError = null) } }
    fun onPasswordChange(password: String) { _uiState.update { it.copy(password = password, passwordError = null) } }
    fun onConfirmPasswordChange(confirm: String) { _uiState.update { it.copy(confirmPassword = confirm, confirmPasswordError = null) } }
    fun onSalaryChange(salary: String) { _uiState.update { it.copy(salary = salary, salaryError = null) } }
    fun onSpecialtyChange(specialty: SpecialtyEntity) { _uiState.update { it.copy(selectedSpecialty = specialty, specialtyError = null) } }
    fun clearSuccess() { _uiState.update { it.copy(registrationSuccess = false) } }

    /**
     * Intenta registrar al nuevo doctor.
     */
    fun registerDoctor() {
        _uiState.update { it.copy(isLoading = true, errorMsg = null) }
        val s = _uiState.value

        // --- Validaciones ---
        val nameError = validateRequired(s.name, "Nombre")
        val emailError = validateEmail(s.email)
        val phoneError = validateRequired(s.phone, "Teléfono")
        val passwordResult = validateRegisterPassword(s.password, s.confirmPassword)
        val salaryDouble = s.salary.toDoubleOrNull()
        var salaryError: String? = null
        if (salaryDouble == null || salaryDouble <= 0) {
            salaryError = "Salario debe ser un número válido."
        }
        val specialtyError = if (s.selectedSpecialty == null) "Debe seleccionar una especialidad." else null

        if (nameError != null || emailError != null || phoneError != null || passwordResult.isFailure || salaryError != null || specialtyError != null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    nameError = nameError,
                    emailError = emailError,
                    phoneError = phoneError,
                    passwordError = passwordResult.exceptionOrNull()?.message?.takeIf { msg -> "débil" in msg },
                    confirmPasswordError = passwordResult.exceptionOrNull()?.message?.takeIf { msg -> "coinciden" in msg },
                    salaryError = salaryError,
                    specialtyError = specialtyError
                )
            }
            return
        }

        // --- Registro ---
        viewModelScope.launch {
            val newDoctor = UserEntity(
                name = s.name,
                email = s.email,
                phone = s.phone,
                password = s.password,
                role = "doctor", // ¡Rol de Doctor!
                specialty = s.selectedSpecialty!!.name, // ¡Especialidad del combo box!
                salary = salaryDouble
            )

            val result = userRepository.register(newDoctor)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        registrationSuccess = true,
                        // Limpia el formulario
                        name = "", email = "", phone = "", password = "",
                        confirmPassword = "", salary = "", selectedSpecialty = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                }
            }
        }
    }
}