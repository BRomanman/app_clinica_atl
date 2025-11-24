package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.SpecialtyRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validateRequired
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class AdminAddDoctorUiState(
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val email: String = "",
    val phone: String = "",
    val salary: String = "",

    val backendSpecialties: List<EspecialidadDto> = emptyList(),
    val newSpecialties: List<String> = emptyList(),
    val selectedSpecialties: List<String> = emptyList(),

    val showNewSpecialtyDialog: Boolean = false,
    val newSpecialtyName: String = "",
    val newSpecialtyError: String? = null,

    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val birthDateError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val salaryError: String? = null,
    val specialtiesError: String? = null,

    val isLoading: Boolean = true,
    val registrationSuccess: Boolean = false,
    val errorMsg: String? = null,
    val createdDoctorName: String? = null
)

class AdminAddDoctorViewModel(
    private val userRepository: UsuariosRepository,
    private val specialtyRepository: SpecialtyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAddDoctorUiState())
    val uiState: StateFlow<AdminAddDoctorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            specialtyRepository.getAllSpecialties()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error cargando especialidades: ${e.message}"
                        )
                    }
                }
                .collect { specialties ->
                    val distinct = specialties.distinctBy { it.name.lowercase() }
                    _uiState.update { it.copy(isLoading = false, backendSpecialties = distinct) }
                }
        }
    }

    private fun sanitizeNumber(raw: String): String =
        raw.replace("\\s".toRegex(), "")
            .replace(".", "")
            .replace(",", "")

    private fun parseSalaryToLong(raw: String): Long? {
        val clean = sanitizeNumber(raw)
        return if (clean.isEmpty()) null else clean.toLongOrNull()
    }

    private fun validateBirthDate(value: String): String? {
        if (value.isBlank()) return "Fecha de nacimiento es requerida."
        val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        return if (!regex.matches(value)) "Formato invalido (aaaa-mm-dd)." else null
    }

    fun onFirstNameChange(name: String) {
        val limited = name.take(50)
        _uiState.update { it.copy(firstName = limited, firstNameError = validateRequired(limited, "Nombre")) }
    }
    fun onLastNameChange(value: String) {
        val limited = value.take(50)
        _uiState.update { it.copy(lastName = limited, lastNameError = validateRequired(limited, "Apellido")) }
    }
    fun onBirthDateChange(value: String) {
        val limited = value.take(10)
        _uiState.update { it.copy(birthDate = limited, birthDateError = validateBirthDate(limited)) }
    }
    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = validateEmail(value)) }
    }
    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value, phoneError = validateChileanPhoneNumber(value)) }
    }
    fun onSalaryChange(value: String) {
        val isValid = parseSalaryToLong(value) != null
        _uiState.update { it.copy(salary = value, salaryError = if (isValid) null else "Debe ser un numero") }
    }

    fun toggleSpecialty(name: String) {
        val cur = _uiState.value.selectedSpecialties
        _uiState.update {
            it.copy(
                selectedSpecialties = if (name in cur) emptyList() else listOf(name),
                specialtiesError = null
            )
        }
    }

    fun openNewSpecialtyDialog() {
        _uiState.update { it.copy(showNewSpecialtyDialog = true, newSpecialtyName = "", newSpecialtyError = null) }
    }
    fun closeNewSpecialtyDialog() {
        _uiState.update { it.copy(showNewSpecialtyDialog = false, newSpecialtyError = null) }
    }
    fun onNewSpecialtyNameChange(value: String) {
        _uiState.update { it.copy(newSpecialtyName = value, newSpecialtyError = null) }
    }
    fun confirmNewSpecialty() {
        val name = _uiState.value.newSpecialtyName.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(newSpecialtyError = "Debe ingresar un nombre") }
            return
        }
        val all = (_uiState.value.backendSpecialties.map { it.name } + _uiState.value.newSpecialties)
            .map { it.trim().lowercase() }
        if (name.lowercase() in all) {
            _uiState.update { it.copy(newSpecialtyError = "La especialidad ya existe") }
            return
        }
        _uiState.update {
            it.copy(
                newSpecialties = it.newSpecialties + name,
                selectedSpecialties = listOf(name),
                showNewSpecialtyDialog = false
            )
        }
    }

    fun clearSuccess() { _uiState.update { it.copy(registrationSuccess = false, createdDoctorName = null) } }

    fun registerDoctor() {
        val s = _uiState.value

        val firstError = validateRequired(s.firstName, "Nombre")
        val lastError  = validateRequired(s.lastName,  "Apellido")
        val birthErr   = validateBirthDate(s.birthDate)
        val emailErr   = validateEmail(s.email)
        val phoneErr   = validateChileanPhoneNumber(s.phone)
        val salaryLong = parseSalaryToLong(s.salary)
        val salaryErr  = if (salaryLong == null || salaryLong <= 0L) "Salario invalido" else null
        val specErr    = if (s.selectedSpecialties.isEmpty()) "Debe seleccionar al menos una" else null

        if (listOf(firstError, lastError, birthErr, emailErr, phoneErr, salaryErr, specErr).any { it != null }) {
            _uiState.update {
                it.copy(
                    firstNameError = firstError, lastNameError = lastError, birthDateError = birthErr,
                    emailError = emailErr, phoneError = phoneErr,
                    salaryError = salaryErr, specialtiesError = specErr
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMsg = null) }

        val passPrefix = s.lastName.padEnd(4, 'x').take(4).replaceFirstChar { it.uppercase() }
        val password = "$passPrefix${Random.nextInt(100, 1000)}@"

        viewModelScope.launch {
            val user = UsuarioDto(
                name = "${s.firstName} ${s.lastName}",
                email = s.email,
                phone = s.phone,
                password = password,
                role = "doctor",
                birthDate = s.birthDate
            )
            val userRes = userRepository.register(user)
            if (userRes.isFailure) {
                _uiState.update { it.copy(isLoading = false, errorMsg = "Error creando usuario: ${userRes.exceptionOrNull()?.message}") }
                return@launch
            }
            val userId = userRes.getOrNull()?.id ?: run {
                _uiState.update { it.copy(isLoading = false, errorMsg = "Error desconocido creando usuario") }
                return@launch
            }

            val doctorRes = userRepository.createDoctorForUser(userId = userId, salary = salaryLong?.toDouble())
            if (doctorRes.isFailure) {
                _uiState.update { it.copy(isLoading = false, errorMsg = "Error creando ficha de doctor") }
                return@launch
            }
            val doctorId = doctorRes.getOrNull()!!

            for (newName in s.newSpecialties) {
                val req = EspecialidadRequestDto(nombre = newName.trim(), doctorId = doctorId)
                val createRes = specialtyRepository.createSpecialty(req)
                if (createRes.isFailure) {
                    _uiState.update { it.copy(isLoading = false, errorMsg = "Error creando especialidad: $newName") }
                    return@launch
                }
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    registrationSuccess = true,
                    createdDoctorName = "${s.firstName} ${s.lastName}".trim(),
                    firstName = "", lastName = "", birthDate = "", email = "", phone = "", salary = "",
                    selectedSpecialties = emptyList(), newSpecialties = emptyList()
                )
            }
        }
    }
}
