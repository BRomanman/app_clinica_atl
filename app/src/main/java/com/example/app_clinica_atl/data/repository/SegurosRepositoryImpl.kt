package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.seguro.SeguroDao
import com.example.app_clinica_atl.data.local.seguro.SeguroEntity
import com.example.app_clinica_atl.data.local.seguro.UsuarioSeguroEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException

/**
 * Implementación del repositorio de Seguros (SegurosApi).
 */
class SegurosRepositoryImpl(
    private val insuranceDao: SeguroDao
) : SegurosRepository {

    override fun getAvailableInsurances(): Flow<List<SeguroEntity>> {
        return insuranceDao.getAllAvailableInsurances()
    }

    override fun getActiveSubscription(patientId: Long): Flow<UsuarioSeguroEntity?> {
        return insuranceDao.getActiveSubscription(patientId)
    }

    override fun getActiveSubscriptionDetails(patientId: Long): Flow<SeguroEntity?> {
        return insuranceDao.getActiveSubscriptionDetails(patientId)
    }

    override suspend fun subscribeToInsurance(patientId: Long, insuranceId: Long): Result<Unit> {
        return try {
            val activeSubscription = insuranceDao.getActiveSubscription(patientId).firstOrNull()
            if (activeSubscription != null) {
                throw IllegalStateException("Ya tienes un seguro activo. Cáncelalo antes de contratar uno nuevo.")
            }

            val newSubscription = UsuarioSeguroEntity(
                patientId = patientId,
                insuranceId = insuranceId,
                status = "activo"
            )
            insuranceDao.subscribePatientToInsurance(newSubscription)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelSubscription(subscriptionId: Long): Result<Unit> {
        return try {
            insuranceDao.updateSubscriptionStatus(subscriptionId, "cancelado")
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
