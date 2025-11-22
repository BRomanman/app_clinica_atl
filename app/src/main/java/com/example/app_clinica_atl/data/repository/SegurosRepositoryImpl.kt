package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.SegurosApi
import com.example.app_clinica_atl.data.remote.dto.BeneficiarioRequest
import com.example.app_clinica_atl.data.remote.dto.ContratoSeguroRequest
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioSeguroDto
import com.example.app_clinica_atl.ui.screen.BeneficiarioForm
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

        emit(null)
    }

    override fun getActiveSubscriptionDetails(patientId: Long): Flow<SeguroDto?> = flow {

        emit(null)
    }

    override suspend fun getInsurancesForPatient(patientId: Long): Result<List<SeguroDto>> {

        return Result.success(emptyList())
    }

    override suspend fun subscribeToInsurance(patientId: Long, insuranceId: Long): Result<Unit> {

        return Result.failure(NotImplementedError("Suscripción remota pendiente de implementar"))
    }

    override suspend fun cancelSubscription(subscriptionId: Long): Result<Unit> {

        return Result.failure(NotImplementedError("Cancelación remota pendiente de implementar"))
    }

    override suspend fun contratarSeguro(
        userId: Long,
        seguroId: Long,
        beneficiarios: List<BeneficiarioForm>,
        metodoPago: String,
        estado: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = ContratoSeguroRequest(
                id_usuario = userId,
                idSeguro = seguroId,
                metodo_pago = metodoPago,
                estado = estado,
                beneficiarios = beneficiarios.map {
                    BeneficiarioRequest(
                        nombre = it.nombre,
                        apellido = it.apellido,
                        rut = it.rut,
                        fecha_nacimiento = it.fechaNacimiento
                    )
                }
            )

            val response = segurosApi.contratarSeguro(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
