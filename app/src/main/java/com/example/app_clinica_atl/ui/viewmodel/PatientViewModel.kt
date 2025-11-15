package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.insurance.InsuranceEntity
import com.example.app_clinica_atl.data.local.insurance.UserInsuranceEntity
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.data.repository.InsuranceRepository
import com.example.app_clinica_atl.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ¡ESTADO DE UI ACTUALIZADO!
 * Ahora contiene los datos del paciente Y los datos de su seguro.
 */
data class PatientProfileUiState(
    val isLoading: Boolean = true,
    val patient: UserEntity? = null,
    val activeInsuranceDetails: InsuranceEntity? = null, // (Para mostrar nombre y precio)
    val activeSubscription: UserInsuranceEntity? = null, // (Para obtener el ID y cancelar)
    val errorMsg: String? = null,
    val successMsg: String? = null
)

/**
 * ¡VIEWMODEL ACTUALIZADO!
 * Ahora recibe InsuranceRepository.
 */
class PatientViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences,
    private val insuranceRepository: InsuranceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientProfileUiState())
    val uiState: StateFlow<PatientProfileUiState> = _uiState.asStateFlow()

    init {
        loadPatientProfileAndInsurance()
    }

    /**
     * Carga el perfil del usuario Y su seguro activo.
     */
    fun loadPatientProfileAndInsurance() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            try {
                // 1. Obtiene el ID del usuario desde DataStore
                val userId = userPreferences.userIdFlow.first()
                if (userId == null) {
                    throw IllegalStateException("Usuario no encontrado. Inicie sesión de nuevo.")
                }

                // 2. Obtiene los datos del usuario (esto solo se ejecuta una vez)
                val userResult = userRepository.getUserById(userId)
                if (userResult.isFailure) {
                    throw userResult.exceptionOrNull() ?: IllegalStateException("Error al cargar perfil")
                }
                _uiState.update { it.copy(patient = userResult.getOrNull()) }

                // 3. Combina los dos Flows de seguros
                // (Se actualizarán automáticamente si cambian)
                val detailsFlow = insuranceRepository.getActiveSubscriptionDetails(userId)
                val subscriptionFlow = insuranceRepository.getActiveSubscription(userId)

                detailsFlow.combine(subscriptionFlow) { details, subscription ->
                    // Creamos un par con ambos resultados
                    Pair(details, subscription)
                }.catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMsg = e.message) }
                }.collect { (details, subscription) ->
                    // Actualizamos el estado con la info del seguro
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            activeInsuranceDetails = details,
                            activeSubscription = subscription,
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
     * Cancela la suscripción activa del usuario.
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
                // El Flow se actualizará solo. Mostramos un mensaje.
                _uiState.update { it.copy(successMsg = "Seguro cancelado con éxito.") }
            } else {
                _uiState.update { it.copy(errorMsg = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMsg = null, successMsg = null) }
    }
}