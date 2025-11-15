package com.example.app_clinica_atl.data.local.appointment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appointment: AppointmentEntity): Long

    @Query("SELECT * FROM appointments")
    suspend fun getAll(): List<AppointmentEntity>

    // --- FUNCIONES AÑADIDAS (REQUERIDAS POR EL REPOSITORIO) ---

    /**
     * Busca si ya existe una cita para un doctor en una fecha y hora específicas.
     * Usado para evitar agendamientos duplicados.
     */
    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId AND date = :date AND time = :time LIMIT 1")
    suspend fun getAppointmentByDoctorDateTime(doctorId: Long, date: String, time: String): AppointmentEntity?

    /**
     * Devuelve una lista de Strings (horas, ej: "09:00", "10:30") que
     * ya están ocupadas para un doctor en una fecha específica.
     */
    @Query("SELECT time FROM appointments WHERE doctorId = :doctorId AND date = :date")
    suspend fun getBookedTimesForDoctorOnDate(doctorId: Long, date: String): List<String>
}