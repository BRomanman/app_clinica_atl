package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.data.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdminManageDoctorUiState(
    val doctorIdQuery: String = "",
    val currentDoctor: DoctorInfo? = null,
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val email: String = "",
    val contactNumber: String = "",
    val password: String = "",
    val consultationRate: String = "",
    val salary: String = "",
    val bonus: String = "",
    val specialtyId: String = "",
    val specialty: String = "",
    val availability: String = "",
    val address: String = "",
    val since: String = "",
    val infoMessage: String? = null,
    val errorMessage: String? = null
) {
    val isDoctorLoaded: Boolean get() = currentDoctor != null
}

class AdminManageDoctorViewModel(
    private val repository: DoctorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminManageDoctorUiState())
    val uiState: StateFlow<AdminManageDoctorUiState> = _uiState.asStateFlow()

    fun onDoctorIdChange(value: String) = updateField { it.copy(doctorIdQuery = value) }
    fun onFirstNameChange(value: String) = updateField { it.copy(firstName = value) }
    fun onLastNameChange(value: String) = updateField { it.copy(lastName = value) }
    fun onBirthDateChange(value: String) = updateField { it.copy(birthDate = value) }
    fun onEmailChange(value: String) = updateField { it.copy(email = value) }
    fun onContactNumberChange(value: String) = updateField { it.copy(contactNumber = value) }
    fun onPasswordChange(value: String) = updateField { it.copy(password = value) }
    fun onConsultationRateChange(value: String) = updateField { it.copy(consultationRate = value) }
    fun onSalaryChange(value: String) = updateField { it.copy(salary = value) }
    fun onBonusChange(value: String) = updateField { it.copy(bonus = value) }
    fun onSpecialtyIdChange(value: String) = updateField { it.copy(specialtyId = value) }
    fun onSpecialtyChange(value: String) = updateField { it.copy(specialty = value) }
    fun onAvailabilityChange(value: String) = updateField { it.copy(availability = value) }
    fun onAddressChange(value: String) = updateField { it.copy(address = value) }
    fun onSinceChange(value: String) = updateField { it.copy(since = value) }

    fun searchDoctor() {
        val query = uiState.value.doctorIdQuery.trim()
        if (query.isEmpty()) {
            _uiState.update {
                it.copy(
                    currentDoctor = null,
                    infoMessage = null,
                    errorMessage = "Ingresa un ID para realizar la busqueda."
                ).clearedForm()
            }
            return
        }

        val doctor = repository.getDoctorById(query)
        if (doctor == null) {
            _uiState.update {
                it.copy(
                    currentDoctor = null,
                    infoMessage = null,
                    errorMessage = "No se encontro un doctor con el ID $query."
                ).clearedForm()
            }
        } else {
            _uiState.update {
                it.copy(
                    currentDoctor = doctor,
                    firstName = doctor.firstName,
                    lastName = doctor.lastName,
                    birthDate = doctor.birthDate,
                    email = doctor.email,
                    contactNumber = doctor.contactNumber,
                    password = doctor.password,
                    consultationRate = doctor.consultationRate,
                    salary = doctor.salary,
                    bonus = doctor.bonus,
                    specialtyId = doctor.specialtyId,
                    specialty = doctor.specialty,
                    availability = doctor.availability,
                    address = doctor.address,
                    since = doctor.since,
                    infoMessage = "Doctor encontrado. Puedes modificar los datos.",
                    errorMessage = null
                )
            }
        }
    }

    fun saveChanges() {
        val state = uiState.value
        val baseDoctor = state.currentDoctor
        if (baseDoctor == null) {
            _uiState.update {
                it.copy(
                    infoMessage = null,
                    errorMessage = "Primero busca un doctor antes de intentar guardar."
                )
            }
            return
        }

        val updated = baseDoctor.copy(
            firstName = state.firstName,
            lastName = state.lastName,
            birthDate = state.birthDate,
            email = state.email,
            contactNumber = state.contactNumber,
            password = state.password,
            consultationRate = state.consultationRate,
            salary = state.salary,
            bonus = state.bonus,
            specialtyId = state.specialtyId,
            specialty = state.specialty,
            availability = state.availability,
            address = state.address,
            since = state.since
        )

        val success = repository.updateDoctor(updated)
        if (success) {
            _uiState.update {
                it.copy(
                    currentDoctor = updated,
                    infoMessage = "Datos del doctor actualizados correctamente.",
                    errorMessage = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    infoMessage = null,
                    errorMessage = "No fue posible actualizar al doctor. Verifica el ID."
                )
            }
        }
    }

    fun deleteDoctor() {
        val doctor = uiState.value.currentDoctor
        if (doctor == null) {
            _uiState.update {
                it.copy(
                    infoMessage = null,
                    errorMessage = "Busca un doctor antes de intentar eliminarlo."
                )
            }
            return
        }

        val success = repository.deleteDoctorById(doctor.id)
        if (success) {
            _uiState.value = AdminManageDoctorUiState(
                infoMessage = "Doctor eliminado correctamente."
            )
        } else {
            _uiState.update {
                it.copy(
                    infoMessage = null,
                    errorMessage = "No fue posible eliminar al doctor. Intenta nuevamente."
                )
            }
        }
    }

    private fun updateField(block: (AdminManageDoctorUiState) -> AdminManageDoctorUiState) {
        _uiState.update { current ->
            block(current).copy(infoMessage = null, errorMessage = null)
        }
    }

    private fun AdminManageDoctorUiState.clearedForm(): AdminManageDoctorUiState = copy(
        firstName = "",
        lastName = "",
        birthDate = "",
        email = "",
        contactNumber = "",
        password = "",
        consultationRate = "",
        salary = "",
        bonus = "",
        specialtyId = "",
        specialty = "",
        availability = "",
        address = "",
        since = ""
    )
}

class AdminManageDoctorViewModelFactory(
    private val repository: DoctorRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminManageDoctorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminManageDoctorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
