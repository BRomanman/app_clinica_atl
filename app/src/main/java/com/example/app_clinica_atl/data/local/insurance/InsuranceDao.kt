package com.example.app_clinica_atl.data.local.insurance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InsuranceDao {

    // --- Funciones para InsuranceEntity (Los tipos de seguro) ---

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInsurance(insurance: InsuranceEntity)

    @Query("SELECT * FROM insurance_table ORDER BY price ASC")
    fun getAllAvailableInsurances(): Flow<List<InsuranceEntity>>

    // --- Funciones para UserInsuranceEntity (Las suscripciones) ---

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun subscribePatientToInsurance(subscription: UserInsuranceEntity)

    @Query("UPDATE user_insurance_table SET status = :newStatus WHERE id = :subscriptionId")
    suspend fun updateSubscriptionStatus(subscriptionId: Long, newStatus: String)

    // --- ¡¡FUNCIONES AÑADIDAS!! ---

    /**
     * Obtiene la *suscripción* activa de un paciente (para saber el ID de la suscripción).
     */
    @Query("SELECT * FROM user_insurance_table WHERE patientId = :patientId AND status = 'activo' LIMIT 1")
    fun getActiveSubscription(patientId: Long): Flow<UserInsuranceEntity?>

    /**
     * Obtiene los *detalles del seguro* (nombre, precio) de la suscripción activa.
     * Hace un "JOIN" entre las dos tablas.
     */
    @Query("""
        SELECT ins.* FROM insurance_table AS ins
        INNER JOIN user_insurance_table AS sub ON ins.id = sub.insuranceId
        WHERE sub.patientId = :patientId AND sub.status = 'activo'
        LIMIT 1
    """)
    fun getActiveSubscriptionDetails(patientId: Long): Flow<InsuranceEntity?>
}