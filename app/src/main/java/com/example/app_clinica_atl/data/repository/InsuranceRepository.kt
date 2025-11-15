package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.insurance.InsuranceEntity
import com.example.app_clinica_atl.data.local.insurance.UserInsuranceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio de Seguros.
 */
interface InsuranceRepository {

    /**
     * Obtiene un Flow con la lista de todos los seguros disponibles.
     */
    fun getAvailableInsurances(): Flow<List<InsuranceEntity>>

    /**
     * Obtiene la suscripción activa del paciente (para ID).
     */
    fun getActiveSubscription(patientId: Long): Flow<UserInsuranceEntity?>

    /**
     * Obtiene los detalles del seguro activo del paciente (para UI).
     */
    fun getActiveSubscriptionDetails(patientId: Long): Flow<InsuranceEntity?>

    /**
     * Suscribe a un paciente a un nuevo seguro.
     */
    suspend fun subscribeToInsurance(patientId: Long, insuranceId: Long): Result<Unit>

    /**
     * Cancela una suscripción existente.
     */
    suspend fun cancelSubscription(subscriptionId: Long): Result<Unit>
}