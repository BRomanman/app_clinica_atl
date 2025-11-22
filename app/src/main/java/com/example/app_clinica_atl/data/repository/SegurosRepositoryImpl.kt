package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.SegurosApi
import com.example.app_clinica_atl.data.remote.dto.ContratoSeguroDto
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioSeguroDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SegurosRepositoryImpl(
    private val segurosApi: SegurosApi = RetrofitClient.segurosApi
) : SegurosRepository {

    override fun getAvailableInsurances(): Flow<List<SeguroDto>> = flow {
        val seguros = withContext(Dispatchers.IO) { segurosApi.getSeguros() }
        emit(seguros)
    }

    override fun getActiveSubscription(patientId: Long): Flow<UsuarioSeguroDto?> = flow {
        emit(null)
    }

    override fun getActiveSubscriptionDetails(patientId: Long): Flow<SeguroDto?> = flow {
        emit(null)
    }

    override suspend fun getInsurancesForPatient(patientId: Long): Result<List<SeguroDto>> {
        return Result.success(emptyList())
    }

    override suspend fun subscribeToInsurance(patientId: Long, insuranceId: Long): Result<Unit> {
        return Result.failure(NotImplementedError("Este endpoint NO existe en el backend"))
    }

    override suspend fun cancelSubscription(subscriptionId: Long): Result<Unit> {
        return Result.failure(NotImplementedError("Backend pendiente"))
    }

    override suspend fun crearContrato(contrato: ContratoSeguroDto): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                segurosApi.crearContrato(contrato)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
