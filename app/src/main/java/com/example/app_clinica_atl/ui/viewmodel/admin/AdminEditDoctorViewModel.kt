package com.example.app_clinica_atl.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.remote.dto.DoctorUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.repository.AdminRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import com.example.app_clinica_atl.domain.validation.validateDateDdMmYyyy
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validateRequired
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class AdminEditDoctorUiState(
    val nombre: String = "",
    val apellido: String = "",
    val birthDate: String = "",
    val email: String = "",
    val telefono: String = "",
    val tarifaConsulta: String = "",
    val sueldo: String = "",
    val bono: String = "",
    val activo: Boolean = true,
    val backendSpecialties: List<EspecialidadDto> = emptyList(),
    val selectedSpecialtyId: Long? = null,
    val selectedSpecialtyName: String = "",
    val specialtyError: String? = null,
    val nombreError: String? = null,
    val apellidoError: String? = null,
    val birthDateError: String? = null,
    val emailError: String? = null,
    val telefonoError: String? = null,
    val tarifaError: String? = null,
    val sueldoError: String? = null,
    val bonoError: String? = null,
    val isLoading: Boolean = true,
    val updateSuccess: Boolean = false,
    val errorMsg: String? = null,
    val doctorId: Long? = null
)

class AdminEditDoctorViewModel(
    private val repository: AdminRepository,
    private val doctorId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminEditDoctorUiState())
    val uiState: StateFlow<AdminEditDoctorUiState> = _uiState.asStateFlow()

    init {
        loadDoctor()
    }

    private fun loadDoctor() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null, updateSuccess = false) }
            val result = repository.getDoctorById(doctorId)
            if (result.isSuccess) {
                val doc = result.getOrNull()!!
                val rawBirth = doc.fechaNacimiento?.trim().orEmpty()
                val birthValue = if (rawBirth.length >= 10) rawBirth.substring(0, 10) else rawBirth
                val birthDisplay = runCatching {
                    LocalDate.parse(birthValue).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                }.getOrElse { birthValue }
                _uiState.update {
                    it.copy(
                        doctorId = doc.id,
                        nombre = doc.nombre.orEmpty(),
                        apellido = doc.apellido.orEmpty(),
                        birthDate = birthDisplay,
                        email = doc.correo.orEmpty(),
                        telefono = doc.telefono.orEmpty(),
                        tarifaConsulta = doc.tarifaConsulta?.toString().orEmpty(),
                        sueldo = doc.sueldo?.toString().orEmpty(),
                        bono = doc.bono?.toString().orEmpty(),
                        activo = doc.activo ?: true,
                        selectedSpecialtyId = doc.idEspecialidad,
                        selectedSpecialtyName = doc.especialidad.orEmpty()
                    )
                }
                loadSpecialtiesForDoctor(doc)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error cargando doctor"
                    )
                }
            }
        }
    }

    private fun loadSpecialtiesForDoctor(doctor: DoctorDto) {
        viewModelScope.launch {
            val result = repository.getAllSpecialties()
            if (result.isSuccess) {
                val specialties = result.getOrNull().orEmpty()
                val specialtyName = specialties.firstOrNull { it.id == doctor.idEspecialidad }?.name
                    ?: doctor.especialidad.orEmpty()
                _uiState.update {
                    it.copy(
                        backendSpecialties = specialties,
                        selectedSpecialtyName = specialtyName,
                        isLoading = false,
                        specialtyError = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        backendSpecialties = emptyList(),
                        isLoading = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error cargando especialidades"
                    )
                }
            }
        }
    }

    fun onNombreChange(value: String) =
        _uiState.update { it.copy(nombre = value, nombreError = null, errorMsg = null, updateSuccess = false) }

    fun onApellidoChange(value: String) =
        _uiState.update { it.copy(apellido = value, apellidoError = null, errorMsg = null, updateSuccess = false) }

    fun onBirthDateChange(value: String) =
        _uiState.update { it.copy(birthDate = value, birthDateError = null, errorMsg = null, updateSuccess = false) }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, emailError = null, errorMsg = null, updateSuccess = false) }

    fun onTelefonoChange(value: String) =
        _uiState.update { it.copy(telefono = value, telefonoError = null, errorMsg = null, updateSuccess = false) }

    fun onTarifaChange(value: String) =
        _uiState.update { it.copy(tarifaConsulta = value, tarifaError = null, errorMsg = null, updateSuccess = false) }

    fun onSueldoChange(value: String) =
        _uiState.update { it.copy(sueldo = value, sueldoError = null, errorMsg = null, updateSuccess = false) }

    fun onBonoChange(value: String) =
        _uiState.update { it.copy(bono = value, bonoError = null, errorMsg = null, updateSuccess = false) }

    fun onActivoChange(value: Boolean) =
        _uiState.update { it.copy(activo = value, errorMsg = null, updateSuccess = false) }

    fun onSpecialtySelected(specialty: EspecialidadDto) =
        _uiState.update {
            it.copy(
                selectedSpecialtyId = specialty.id,
                selectedSpecialtyName = specialty.name,
                specialtyError = null,
                errorMsg = null,
                updateSuccess = false
            )
        }

    fun saveChanges() {
        viewModelScope.launch {
            val state = _uiState.value
            val nombreError = validateRequired(state.nombre, "Nombre")
            val apellidoError = validateRequired(state.apellido, "Apellido")
            val birthDateError = validateDateDdMmYyyy(state.birthDate, "Fecha de nacimiento")
            val emailError = validateEmail(state.email)
            val telefonoError = validateChileanPhoneNumber(state.telefono)
            val tarifaValue = parseInt(state.tarifaConsulta)
            val tarifaError = if (tarifaValue == null || tarifaValue <= 0) "Tarifa invalida" else null
            val sueldoValue = parseLong(state.sueldo)
            val sueldoError = if (sueldoValue == null || sueldoValue <= 0L) "Salario invalido" else null
            val bonoValue = parseLongAllowBlank(state.bono)
            val bonoError = if (bonoValue == null || bonoValue < 0L) "Bono invalido" else null
            val specialtyError = if (state.selectedSpecialtyId == null) "Debe seleccionar una especialidad" else null

            if (listOf(
                    nombreError, apellidoError, birthDateError, emailError,
                    telefonoError, tarifaError, sueldoError, bonoError, specialtyError
                ).any { it != null }
            ) {
                _uiState.update {
                    it.copy(
                        nombreError = nombreError,
                        apellidoError = apellidoError,
                        birthDateError = birthDateError,
                        emailError = emailError,
                        telefonoError = telefonoError,
                        tarifaError = tarifaError,
                        sueldoError = sueldoError,
                        bonoError = bonoError,
                        specialtyError = specialtyError,
                        isLoading = false,
                        updateSuccess = false
                    )
                }
                return@launch
            }

            val birthIso = normalizeBirthDate(state.birthDate)
            if (birthIso == null) {
                _uiState.update { it.copy(birthDateError = "Fecha invalida (dd-MM-aaaa)") }
                return@launch
            }

            val request = DoctorUpdateRequestDto(
                nombre = state.nombre.trim(),
                apellido = state.apellido.trim(),
                fechaNacimiento = birthIso,
                correo = state.email.trim(),
                telefono = state.telefono.trim(),
                idEspecialidad = state.selectedSpecialtyId!!,
                tarifaConsulta = tarifaValue!!,
                sueldo = sueldoValue!!,
                bono = bonoValue!!,
                activo = state.activo
            )

            _uiState.update { it.copy(isLoading = true, errorMsg = null, updateSuccess = false) }
            val result = repository.updateDoctor(doctorId, request)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, updateSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error actualizando doctor"
                    )
                }
            }
        }
    }

    private fun sanitizeNumber(raw: String): String =
        raw.replace("\\s".toRegex(), "")
            .replace(".", "")
            .replace(",", "")

    private fun parseInt(raw: String): Int? {
        val clean = sanitizeNumber(raw)
        return if (clean.isEmpty()) null else clean.toIntOrNull()
    }

    private fun parseLong(raw: String): Long? {
        val clean = sanitizeNumber(raw)
        return if (clean.isEmpty()) null else clean.toLongOrNull()
    }

    private fun parseLongAllowBlank(raw: String): Long? {
        val clean = sanitizeNumber(raw)
        return when {
            clean.isEmpty() -> 0L
            else -> clean.toLongOrNull()
        }
    }

    private fun normalizeBirthDate(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return null
        val ddMm = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return runCatching { LocalDate.parse(trimmed, ddMm).format(DateTimeFormatter.ISO_DATE) }
            .getOrElse {
                runCatching { LocalDate.parse(trimmed).format(DateTimeFormatter.ISO_DATE) }.getOrNull()
            }
    }
}

class AdminEditDoctorViewModelFactory(
    private val repo: AdminRepository,
    private val id: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminEditDoctorViewModel(repo, id) as T
    }
}
