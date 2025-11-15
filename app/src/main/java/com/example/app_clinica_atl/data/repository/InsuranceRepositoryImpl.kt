package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.insurance.InsuranceDao
import com.example.app_clinica_atl.data.local.insurance.InsuranceEntity
import com.example.app_clinica_atl.data.local.insurance.UserInsuranceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException

/**
 * Implementación del repositorio de Seguros.
 */
class InsuranceRepositoryImpl(
    private val insuranceDao: InsuranceDao
) : InsuranceRepository {

    override fun getAvailableInsurances(): Flow<List<InsuranceEntity>> {
        return insuranceDao.getAllAvailableInsurances()
    }

    // --- ¡¡NUEVA FUNCIÓN IMPLEMENTADA!! ---
    override fun getActiveSubscription(patientId: Long): Flow<UserInsuranceEntity?> {
        return insuranceDao.getActiveSubscription(patientId)
    }

    // --- ¡¡NUEVA FUNCIÓN IMPLEMENTADA!! ---
    override fun getActiveSubscriptionDetails(patientId: Long): Flow<InsuranceEntity?> {
        return insuranceDao.getActiveSubscriptionDetails(patientId)
    }

    override suspend fun subscribeToInsurance(patientId: Long, insuranceId: Long): Result<Unit> {
        return try {
            val activeSubscription = insuranceDao.getActiveSubscription(patientId).firstOrNull()
            if (activeSubscription != null) {
                throw IllegalStateException("Ya tienes un seguro activo. Cáncelalo antes de contratar uno nuevo.")
            }

            val newSubscription = UserInsuranceEntity(
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