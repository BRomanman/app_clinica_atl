package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.appointment.AppointmentDetails
import com.example.app_clinica_atl.data.local.insurance.InsuranceEntity
import com.example.app_clinica_atl.data.local.insurance.UserInsuranceEntity
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.data.repository.AppointmentRepository
import com.example.app_clinica_atl.data.repository.InsuranceRepository
import com.example.app_clinica_atl.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ¡ESTADO DE UI ACTUALIZADO!
 * Ahora contiene las citas.
 */
data class PatientProfileUiState(
    val isLoading: Boolean = true,
    val patient: UserEntity? = null,
    // Datos de Seguro
    val activeInsuranceDetails: InsuranceEntity? = null,
    val activeSubscription: UserInsuranceEntity? = null,
    // ¡AÑADIDO! Datos de Citas
    val activeAppointments: List<AppointmentDetails> = emptyList(),
    // Mensajes
    val errorMsg: String? = null,
    val successMsg: String? = null
)

/**
 * ¡VIEWMODEL ACTUALIZADO!
 * Ahora recibe AppointmentRepository.
 */
class PatientViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences,
    private val insuranceRepository: InsuranceRepository,
    private val appointmentRepository: AppointmentRepository // <-- ¡AÑADIDO!
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientProfileUiState())
    val uiState: StateFlow<PatientProfileUiState> = _uiState.asStateFlow()

    init {
        loadPatientProfileData()
    }

    /**
     * Carga TODOS los datos del perfil (usuario, seguro y citas).
     */
    private fun loadPatientProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            try {
                // 1. Obtiene el ID del usuario
                val userId = userPreferences.userIdFlow.first()
                if (userId == null) {
                    throw IllegalStateException("Usuario no encontrado. Inicie sesión de nuevo.")
                }

                // 2. Obtiene los datos del usuario (se ejecuta una vez)
                val userResult = userRepository.getUserById(userId)
                if (userResult.isFailure) {
                    throw userResult.exceptionOrNull() ?: IllegalStateException("Error al cargar perfil")
                }
                _uiState.update { it.copy(patient = userResult.getOrNull()) }

                // 3. Combina los TRES Flows (Seguro, Suscripción y Citas)
                val detailsFlow = insuranceRepository.getActiveSubscriptionDetails(userId)
                val subscriptionFlow = insuranceRepository.getActiveSubscription(userId)
                val appointmentsFlow = appointmentRepository.getAppointmentsForPatient(userId) // <-- ¡AÑADIDO!

                combine(detailsFlow, subscriptionFlow, appointmentsFlow) { details, subscription, appointments ->
                    // Creamos un objeto anónimo para guardar los 3 resultados
                    object {
                        val insDetails = details
                        val insSub = subscription
                        val appts = appointments
                    }
                }.catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMsg = e.message) }
                }.collect { combinedData ->
                    // Actualizamos el estado con toda la info
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            activeInsuranceDetails = combinedData.insDetails,
                            activeSubscription = combinedData.insSub,
                            activeAppointments = combinedData.appts, // <-- ¡AÑADIDO!
                            errorMsg = null
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    /**
     * Cancela la suscripción de seguro.
     */
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

    // --- ¡¡FUNCIÓN AÑADIDA!! ---
    /**
     * Cancela una cita.
     */
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