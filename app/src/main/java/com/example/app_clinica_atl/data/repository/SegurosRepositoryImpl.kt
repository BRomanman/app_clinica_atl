package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.SegurosApi
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioSeguroDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * Implementación del repositorio de Seguros basada en DTOs.
 */
class SegurosRepositoryImpl(
    private val segurosApi: SegurosApi = RetrofitClient.segurosApi
) : SegurosRepository {

    override fun getAvailableInsurances(): Flow<List<SeguroDto>> = flow {
        val seguros = withContext(Dispatchers.IO) { segurosApi.getSeguros() }
        emit(seguros)
    }

    override fun getActiveSubscription(patientId: Long): Flow<UsuarioSeguroDto?> = flow {
        // TODO: Implementar cuando exista endpoint de subscripciones
        emit(null)
    }

    override fun getActiveSubscriptionDetails(patientId: Long): Flow<SeguroDto?> = flow {
        // TODO: Implementar cuando exista endpoint de subscripciones
        emit(null)
    }

    override suspend fun subscribeToInsurance(patientId: Long, insuranceId: Long): Result<Unit> {
        // TODO: Consumir endpoint real (POST) cuando esté disponible
        return Result.failure(NotImplementedError("Suscripción remota pendiente de implementar"))
    }

    override suspend fun cancelSubscription(subscriptionId: Long): Result<Unit> {
        // TODO: Consumir endpoint real (PATCH/DELETE) cuando esté disponible
        return Result.failure(NotImplementedError("Cancelación remota pendiente de implementar"))
    }
}
