package com.example.app_clinica_atl.data.local.cita

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appointment: CitaEntity): Long

    @Query("SELECT * FROM appointments")
    suspend fun getAll(): List<CitaEntity>

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId AND date = :date AND time = :time LIMIT 1")
    suspend fun getAppointmentByDoctorDateTime(doctorId: Long, date: String, time: String): CitaEntity?

    @Query("SELECT time FROM appointments WHERE doctorId = :doctorId AND date = :date")
    suspend fun getBookedTimesForDoctorOnDate(doctorId: Long, date: String): List<String>

    // --- ¡¡FUNCIONES AÑADIDAS PARA "MIS CITAS"!! ---

    /**
     * Obtiene una lista de todas las citas "agendadas" de un paciente,
     * uniéndolas con la tabla de usuarios (doctores) para obtener sus nombres.
     * Usa la clase DTO 'CitaDetalle' para guardar el resultado.
     */
    @Query("""
        SELECT
            a.id as appointmentId,
            u.name as doctorName,
            u.specialty as doctorSpecialty,
            a.date,
            a.time,
            a.status
        FROM appointments AS a
        INNER JOIN user_table AS u ON a.doctorId = u.id
        WHERE a.patientId = :patientId AND a.status = 'agendada'
        ORDER BY a.date, a.time ASC
    """)
    fun getActiveAppointmentsForPatient(patientId: Long): Flow<List<CitaDetalle>>

    /**
     * Actualiza el estado de una cita (para cancelarla).
     */
    @Query("UPDATE appointments SET status = 'cancelada' WHERE id = :appointmentId")
    suspend fun cancelAppointment(appointmentId: Long)
}
