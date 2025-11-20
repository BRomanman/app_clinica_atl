package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.dto.CitaDetalleDto
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio de Citas (alineado con CitasApi).
 */
interface CitasRepository {

    /**
     * Intenta agendar una nueva cita vía API.
     */
    suspend fun bookAppointment(appointment: CitaDto): Result<CitaDto>

    /**
     * Obtiene una lista de las horas (Strings) que YA están reservadas
     * para un doctor específico en una fecha específica.
     */
    suspend fun getBookedTimes(doctorId: Long, date: String): Result<List<String>>

    /**
     * Obtiene un Flow con todas las citas activas de un paciente.
     */
    fun getAppointmentsForPatient(patientId: Long): Flow<List<CitaDetalleDto>>

    /**
     * Obtiene las citas de un paciente en una sola consulta.
     */
    suspend fun getAppointmentsForPatientOnce(patientId: Long): Result<List<CitaDto>>

    /**
     * Cancela una cita.
     */
    suspend fun cancelAppointment(appointmentId: Long): Result<Unit>
}
