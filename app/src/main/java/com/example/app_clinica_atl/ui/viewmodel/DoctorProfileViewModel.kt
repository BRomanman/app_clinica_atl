package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.data.repository.DoctorRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- DATOS POR DEFECTO ---
private val DEFAULT_DOCTOR_INFO = DoctorInfo(
    id = "000",
    firstName = "Víctor",
    lastName = "Rosendo",
    birthDate = "2000-05-10",
    email = "victor@duoc.cl",
    contactNumber = "+56922222222",
    password = "123456", // Omitir en producción, solo para el estado de mock
    consultationRate = "40000",
    salary = "2000000",
    bonus = "150000",
    specialtyId = "MED",
    specialty = "Medicina General",
    availability = "Martes y Jueves, 10:00 - 18:00",
    address = "Av. Clínica Duoc 555, Santiago",
    since = "2020"
)

// --- 1. ESTADO DE LA PANTALLA ---
data class DoctorProfileUiState(
    val doctorInfo: DoctorInfo = DEFAULT_DOCTOR_INFO,
    val appointmentsCompleted: Int = 0,
    val newPatientsThisMonth: Int = 0,
    val photoUriString: String? = null,
    val isLoading: Boolean = true,
    // *** NUEVOS ESTADOS DE BOTÓN ***
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class DoctorProfileViewModel(
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorProfileUiState())
    val uiState: StateFlow<DoctorProfileUiState> = _uiState.asStateFlow()

    init {
        loadDoctorData()
    }

    private fun loadDoctorData() {
        viewModelScope.launch {
            val loggedDoctor = doctorRepository.getDoctorByEmail("victor@duoc.cl")

            if (loggedDoctor != null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        doctorInfo = loggedDoctor,
                        appointmentsCompleted = 45,
                        newPatientsThisMonth = 12,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(
                    appointmentsCompleted = 30,
                    newPatientsThisMonth = 8,
                    isLoading = false
                ) }
            }
        }
    }

    // *** FUNCIÓN: Guardar el perfil ***
    fun saveProfile() {
        if (uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            // SIMULACIÓN DE LLAMADA A LA BASE DE DATOS (2 segundos)
            delay(2000)

            // En una aplicación real, aquí llamarías a repository.updateDoctor(uiState.value.doctorInfo)

            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    fun updatePhotoUri(uriString: String?) {
        _uiState.update { it.copy(photoUriString = uriString, saveSuccess = false) }
    }

    // Handlers para actualizar campos de contacto (limpia la bandera de éxito al cambiar)
    fun updateContactNumber(newNumber: String) {
        _uiState.update {
            it.copy(doctorInfo = it.doctorInfo.copy(contactNumber = newNumber), saveSuccess = false)
        }
    }
    fun updateAddress(newAddress: String) {
        _uiState.update {
            it.copy(doctorInfo = it.doctorInfo.copy(address = newAddress), saveSuccess = false)
        }
    }
    fun updateEmail(newEmail: String) {
        _uiState.update {
            it.copy(doctorInfo = it.doctorInfo.copy(email = newEmail), saveSuccess = false)
        }
    }
}
