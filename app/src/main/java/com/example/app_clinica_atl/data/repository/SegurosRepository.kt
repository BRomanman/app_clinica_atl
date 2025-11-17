package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.seguro.SeguroEntity
import com.example.app_clinica_atl.data.local.seguro.UsuarioSeguroEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio de Seguros (alineado con SegurosApi).
 */
interface SegurosRepository {

    /**
     * Obtiene un Flow con la lista de todos los seguros disponibles.
     */
    fun getAvailableInsurances(): Flow<List<SeguroEntity>>

    /**
     * Obtiene la suscripción activa del paciente (para ID).
     */
    fun getActiveSubscription(patientId: Long): Flow<UsuarioSeguroEntity?>

    /**
     * Obtiene los detalles del seguro activo del paciente (para UI).
     */
    fun getActiveSubscriptionDetails(patientId: Long): Flow<SeguroEntity?>

    /**
     * Suscribe a un paciente a un nuevo seguro.
     */
    suspend fun subscribeToInsurance(patientId: Long, insuranceId: Long): Result<Unit>

    /**
     * Cancela una suscripción existente.
     */
    suspend fun cancelSubscription(subscriptionId: Long): Result<Unit>
}
