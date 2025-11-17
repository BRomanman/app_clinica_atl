package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.cita.CitaDetalle
import com.example.app_clinica_atl.data.local.cita.CitaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio de Citas (alineado con CitasApi).
 */
interface CitasRepository {

    /**
     * Intenta agendar una nueva cita en la base de datos.
     */
    suspend fun bookAppointment(appointment: CitaEntity): Result<Long>

    /**
     * Obtiene una lista de las horas (Strings) que YA están reservadas
     * para un doctor específico en una fecha específica.
     */
    suspend fun getBookedTimes(doctorId: Long, date: String): Result<List<String>>

    /**
     * Obtiene un Flow con todas las citas activas de un paciente.
     */
    fun getAppointmentsForPatient(patientId: Long): Flow<List<CitaDetalle>>

    /**
     * Cancela una cita.
     */
    suspend fun cancelAppointment(appointmentId: Long): Result<Unit>
}
