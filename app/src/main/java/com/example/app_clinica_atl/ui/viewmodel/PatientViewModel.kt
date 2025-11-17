package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.cita.CitaDetalle
import com.example.app_clinica_atl.data.local.seguro.SeguroEntity
import com.example.app_clinica_atl.data.local.seguro.UsuarioSeguroEntity
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.SegurosRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PatientProfileUiState(
    val isLoading: Boolean = true,
    val patient: UsuarioEntity? = null,
    val activeInsuranceDetails: SeguroEntity? = null,
    val activeSubscription: UsuarioSeguroEntity? = null,
    val activeAppointments: List<CitaDetalle> = emptyList(),
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class PatientViewModel(
    private val userRepository: UsuariosRepository,
    private val userPreferences: UserPreferences,
    private val insuranceRepository: SegurosRepository,
    private val appointmentRepository: CitasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientProfileUiState())
    val uiState: StateFlow<PatientProfileUiState> = _uiState

    // --- ¡¡LÓGICA ACTUALIZADA!! ---
    // Usamos 'flatMapLatest' para reaccionar al cambio de usuario (login/logout)
    private val _profileDataFlow = userPreferences.userIdFlow
        .flatMapLatest { userId ->
            if (userId == null) {
                // Si no hay usuario, emitimos un estado de error/vacío
                flowOf(
                    PatientProfileUiState(
                        isLoading = false,
                        errorMsg = "Usuario no encontrado. Inicie sesión."
                    )
                )
            } else {
                // Si hay usuario, combinamos sus 3 fuentes de datos
                combine(
                    userRepository.getUserByIdAsFlow(userId),
                    insuranceRepository.getActiveSubscriptionDetails(userId),
                    insuranceRepository.getActiveSubscription(userId),
                    appointmentRepository.getAppointmentsForPatient(userId)
                ) { patient, insuranceDetails, insuranceSub, appointments ->
                    PatientProfileUiState(
                        isLoading = false,
                        patient = patient,
                        activeInsuranceDetails = insuranceDetails,
                        activeSubscription = insuranceSub,
                        activeAppointments = appointments
                    )
                }
            }
        }.catch { e ->
            emit(PatientProfileUiState(isLoading = false, errorMsg = e.message))
        }

    init {
        // El ViewModel ahora simplemente colecta el Flow combinado
        viewModelScope.launch {
            _profileDataFlow.collect { state ->
                _uiState.update {
                    // Mantenemos los mensajes de éxito/error que pudieran existir
                    // mientras actualizamos los datos
                    it.copy(
                        isLoading = state.isLoading,
                        patient = state.patient,
                        activeInsuranceDetails = state.activeInsuranceDetails,
                        activeSubscription = state.activeSubscription,
                        activeAppointments = state.activeAppointments,
                        errorMsg = state.errorMsg
                    )
                }
            }
        }
    }

    // --- Lógica de cancelación (sin cambios) ---

    fun cancelSubscription() {
        viewModelScope.launch {
            val subscriptionId = _uiState.value.activeSubscription?.id
            if (subscriptionId == null) {
                _uiState.update { it.copy(errorMsg = "No se encontró suscripción para cancelar.") }
                return@launch
            }
            val result = insuranceRepository.cancelSubscription(subscriptionId)
            if (result.isSuccess) {
                _uiState.update { it.copy(successMsg = "Seguro cancelado con éxito.") }
            } else {
                _uiState.update { it.copy(errorMsg = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            val result = appointmentRepository.cancelAppointment(appointmentId)
            if (result.isSuccess) {
                _uiState.update { it.copy(successMsg = "Cita cancelada con éxito.") }
            } else {
                _uiState.update { it.copy(errorMsg = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMsg = null, successMsg = null) }
    }
}
