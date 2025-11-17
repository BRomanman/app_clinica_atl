package com.example.app_clinica_atl.ui.viewmodel

import android.net.Uri
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PatientProfileUiState(
    val isLoading: Boolean = true,
    val patient: UserEntity? = null,
    val activeInsuranceDetails: InsuranceEntity? = null,
    val activeSubscription: UserInsuranceEntity? = null,
    val activeAppointments: List<AppointmentDetails> = emptyList(),
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class PatientViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences,
    private val insuranceRepository: InsuranceRepository,
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    // --- LÓGICA REACTIVA ---
    private val _messageState = MutableStateFlow(Pair<String?, String?>(null, null))

    val uiState: StateFlow<PatientProfileUiState> = userPreferences.userIdFlow
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf(
                    PatientProfileUiState(
                        isLoading = false,
                        errorMsg = "Usuario no encontrado. Inicie sesión."
                    )
                )
            } else {
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
        .combine(_messageState) { dataState, messageState ->
            dataState.copy(
                errorMsg = messageState.first,
                successMsg = messageState.second
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PatientProfileUiState()
        )
    // --- FIN LÓGICA REACTIVA ---


    fun cancelSubscription() {
        viewModelScope.launch {
            val subscriptionId = uiState.value.activeSubscription?.id
            if (subscriptionId == null) {
                _messageState.update { it.copy(first = "No se encontró suscripción para cancelar.") }
                return@launch
            }
            val result = insuranceRepository.cancelSubscription(subscriptionId)
            if (result.isSuccess) {
                _messageState.update { it.copy(second = "Seguro cancelado con éxito.") }
            } else {
                _messageState.update { it.copy(first = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            val result = appointmentRepository.cancelAppointment(appointmentId)
            if (result.isSuccess) {
                _messageState.update { it.copy(second = "Cita cancelada con éxito.") }
            } else {
                // --- ¡¡AQUÍ ESTÁ LA CORRECCIÓN!! ---
                _messageState.update { it.copy(first = result.exceptionOrNull()?.message) } // Era _messageS
            }
        }
    }

    fun clearMessages() {
        _messageState.update { Pair(null, null) }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            val userId = userPreferences.userIdFlow.firstOrNull()
            if (userId == null) {
                _messageState.update { it.copy(first = "No se pudo encontrar al usuario.") }
                return@launch
            }
            val result = userRepository.updateProfileImageUrl(userId, uri.toString())
            if (result.isFailure) {
                _messageState.update { it.copy(first = result.exceptionOrNull()?.message) }
            }
        }
    }
}