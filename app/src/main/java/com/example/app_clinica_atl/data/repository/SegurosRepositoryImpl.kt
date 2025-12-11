package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.SegurosApi
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioSeguroDto
import com.example.app_clinica_atl.data.remote.dto.ContratoSeguroDto
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
        val response = withContext(Dispatchers.IO) { segurosApi.getSeguros() }
        val seguros = if (response.isSuccessful) response.body().orEmpty() else emptyList()
        emit(seguros)
    }

    override fun getActiveSubscription(patientId: Long): Flow<UsuarioSeguroDto?> = flow {
        val contrato = withContext(Dispatchers.IO) {
            runCatching {
                segurosApi.contratosPorUsuario(patientId).body().orEmpty()
            }.getOrElse { emptyList() }
                .firstOrNull { !it.estado.equals("CANCELADO", true) }
        }
        emit(
            contrato?.let {
                UsuarioSeguroDto(
                    id = it.id,
                    patientId = it.idUsuario,
                    insuranceId = it.idSeguro,
                    status = it.estado
                )
            }
        )
    }

    override fun getActiveSubscriptionDetails(patientId: Long): Flow<SeguroDto?> = flow {
        val contrato = withContext(Dispatchers.IO) {
            runCatching { segurosApi.contratosPorUsuario(patientId).body().orEmpty() }.getOrElse { emptyList() }
                .firstOrNull { !it.estado.equals("CANCELADO", true) }
        }
        val seguro = contrato?.let {
            runCatching { segurosApi.getSeguroById(it.idSeguro).body() }.getOrNull()
        }
        emit(seguro)
    }

    override suspend fun getInsurancesForPatient(patientId: Long): Result<List<SeguroDto>> {

        return runCatching {
            val contratos = segurosApi.contratosPorUsuario(patientId).body().orEmpty()
                .filter { !it.estado.equals("CANCELADO", true) }
            contratos.mapNotNull { contrato ->
                runCatching { segurosApi.getSeguroById(contrato.idSeguro).body() }.getOrNull()
            }
        }
    }

    override suspend fun subscribeToInsurance(patientId: Long, insuranceId: Long): Result<Unit> {

        return Result.failure(NotImplementedError("Suscripción remota pendiente de implementar"))
    }

    override suspend fun cancelSubscription(subscriptionId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = segurosApi.cancelarContrato(subscriptionId)
                if (!response.isSuccessful) throw IllegalStateException("No se pudo cancelar el contrato.")
                Unit
            }
        }
    }

    override suspend fun crearContrato(contrato: ContratoSeguroDto): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val response = segurosApi.crearContrato(contrato)
            if (!response.isSuccessful) throw IllegalStateException("No se pudo crear el contrato.")
            Unit
        }
    }
}
