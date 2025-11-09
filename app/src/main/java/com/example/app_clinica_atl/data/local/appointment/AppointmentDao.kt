package com.example.app_clinica_atl.data.local.appointment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    // (Esta consulta sigue igual, para el perfil del paciente)
    @Query("SELECT * FROM appointments WHERE patient_id = :patientId ORDER BY date DESC, time DESC")
    fun getAppointmentsForUser(patientId: Long): Flow<List<AppointmentEntity>>

    // --- ¡NUEVA CONSULTA! ---
    // (Esta es para la agenda del doctor)
    @Query("SELECT * FROM appointments WHERE doctor_id = :doctorId ORDER BY date ASC, time ASC")
    fun getAppointmentsForDoctor(doctorId: String): Flow<List<AppointmentEntity>>
    // --- FIN NUEVA CONSULTA ---

    @Query("DELETE FROM appointments WHERE id = :appointmentId")
    suspend fun deleteAppointmentById(appointmentId: Long)
}