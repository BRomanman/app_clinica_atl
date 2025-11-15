package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.specialty.SpecialtyEntity
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.data.repository.SpecialtyRepository
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber // <-- ¡IMPORT AÑADIDO!
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validateRequired
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Estado de la UI para la pantalla de añadir doctor.
 * ¡ACTUALIZADO!
 */
data class AdminAddDoctorUiState(
    // Datos del formulario
    val firstName: String = "",         // <-- CAMBIO
    val lastName: String = "",          // <-- CAMBIO
    val email: String = "",
    val phone: String = "",
    val salary: String = "",
    val selectedSpecialty: SpecialtyEntity? = null,

    // Lista para el combo box
    val specialties: List<SpecialtyEntity> = emptyList(),

    // Errores de validación
    val firstNameError: String? = null, // <-- CAMBIO
    val lastNameError: String? = null,  // <-- CAMBIO
    val emailError: String? = null,
    val phoneError: String? = null,
    val salaryError: String? = null,
    val specialtyError: String? = null,

    // (Campos de contraseña eliminados)

    // Estado general
    val isLoading: Boolean = true,
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
        // Carga la lista de especialidades (sin cambios)
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

    // --- ¡¡HANDLERS DE UI ACTUALIZADOS!! ---
    fun onFirstNameChange(name: String) {
        val error = validateRequired(name, "Nombre")
        _uiState.update { it.copy(firstName = name, firstNameError = error) }
    }
    fun onLastNameChange(name: String) {
        val error = validateRequired(name, "Apellido")
        _uiState.update { it.copy(lastName = name, lastNameError = error) }
    }
    fun onEmailChange(email: String) {
        val error = validateEmail(email)
        _uiState.update { it.copy(email = email, emailError = error) }
    }
    fun onPhoneChange(phone: String) {
        val error = validateChileanPhoneNumber(phone)
        _uiState.update { it.copy(phone = phone, phoneError = error) }
    }
    fun onSalaryChange(salary: String) {
        val error = if (salary.toDoubleOrNull() == null) "Debe ser un número" else null
        _uiState.update { it.copy(salary = salary, salaryError = error) }
    }
    fun onSpecialtyChange(specialty: SpecialtyEntity) {
        _uiState.update { it.copy(selectedSpecialty = specialty, specialtyError = null) }
    }
    fun clearSuccess() { _uiState.update { it.copy(registrationSuccess = false) } }

    /**
     * ¡¡FUNCIÓN DE REGISTRO ACTUALIZADA!!
     */
    fun registerDoctor() {
        _uiState.update { it.copy(isLoading = true, errorMsg = null) }
        val s = _uiState.value

        // --- Validaciones finales ---
        val nameError = validateRequired(s.firstName, "Nombre")
        val lastNameError = validateRequired(s.lastName, "Apellido")
        val emailError = validateEmail(s.email)
        val phoneError = validateChileanPhoneNumber(s.phone)
        val salaryDouble = s.salary.toDoubleOrNull()
        val salaryError = if (salaryDouble == null || salaryDouble <= 0) "Salario debe ser un número válido." else null
        val specialtyError = if (s.selectedSpecialty == null) "Debe seleccionar una especialidad." else null

        if (nameError != null || lastNameError != null || emailError != null || phoneError != null || salaryError != null || specialtyError != null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    firstNameError = nameError,
                    lastNameError = lastNameError,
                    emailError = emailError,
                    phoneError = phoneError,
                    salaryError = salaryError,
                    specialtyError = specialtyError
                )
            }
            return
        }

        // --- ¡¡LÓGICA DE CONTRASEÑA AUTOMÁTICA!! ---
        // 1. Tomamos las primeras 4 letras del apellido (o rellenamos si es corto)
        val passPrefix = s.lastName.padEnd(4, 'x').take(4)
        // 2. Capitalizamos la primera letra
        val formattedPrefix = passPrefix.replaceFirstChar { it.uppercase() }
        // 3. Generamos 3 números al azar
        val passNumbers = Random.nextInt(100, 1000) // (ej: 777)
        // 4. Creamos la contraseña
        val generatedPassword = "$formattedPrefix$passNumbers@" // (Ej: "Roma777@")
        // --- FIN LÓGICA ---

        viewModelScope.launch {
            val newDoctor = UserEntity(
                name = "${s.firstName} ${s.lastName}", // Combinamos los nombres
                email = s.email,
                phone = s.phone,
                password = generatedPassword, // <-- ¡Usamos la contraseña generada!
                role = "doctor",
                specialty = s.selectedSpecialty!!.name,
                salary = salaryDouble
            )

            val result = userRepository.register(newDoctor)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        registrationSuccess = true,
                        // Limpia el formulario
                        firstName = "", lastName = "", email = "", phone = "",
                        salary = "", selectedSpecialty = null
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