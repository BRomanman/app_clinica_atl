package com.example.app_clinica_atl.data.local.appointment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    /**
     * Inserta una nueva cita. Si ya existe, la reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    /**
     * Obtiene un Flow (flujo) de todas las citas para un ID de paciente específico.
     * Usar un Flow significa que la UI se actualizará automáticamente si una cita se borra.
     */
    @Query("SELECT * FROM appointments WHERE patient_id = :patientId ORDER BY date DESC, time DESC")
    fun getAppointmentsForUser(patientId: Long): Flow<List<AppointmentEntity>>

    /**
     * Borra una cita usando su ID.
     */
    @Query("DELETE FROM appointments WHERE id = :appointmentId")
    suspend fun deleteAppointmentById(appointmentId: Long)
}