package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioSeguroDto
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio de Seguros (alineado con SegurosApi).
 */
interface SegurosRepository {

    /**
     * Obtiene un Flow con la lista de todos los seguros disponibles.
     */
    fun getAvailableInsurances(): Flow<List<SeguroDto>>

    /**
     * Obtiene la suscripción activa del paciente (para ID).
     */
    fun getActiveSubscription(patientId: Long): Flow<UsuarioSeguroDto?>

    /**
     * Obtiene los detalles del seguro activo del paciente (para UI).
     */
    fun getActiveSubscriptionDetails(patientId: Long): Flow<SeguroDto?>

    /**
     * Obtiene los seguros asociados a un paciente desde la API.
     */
    suspend fun getInsurancesForPatient(patientId: Long): Result<List<SeguroDto>>

    /**
     * Suscribe a un paciente a un nuevo seguro.
     */
    suspend fun subscribeToInsurance(patientId: Long, insuranceId: Long): Result<Unit>

    /**
     * Cancela una suscripción existente.
     */
    suspend fun cancelSubscription(subscriptionId: Long): Result<Unit>
}
