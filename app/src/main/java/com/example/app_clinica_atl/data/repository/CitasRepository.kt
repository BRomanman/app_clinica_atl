package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.CitaDto
import com.example.app_clinica_atl.data.remote.dto.CitaDetalleDto
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio de Citas (alineado con CitasApi).
 */
interface CitasRepository {

    /**
     * Lista las citas de un paciente.
     */
    suspend fun getCitasUsuario(idUsuario: Long): List<CitaDto>

    /**
     * Devuelve los horarios disponibles de un doctor para una fecha dada.
     */
    suspend fun getHorariosDisponibles(doctorId: Long, date: String): List<CitaDto>

    /**
     * Reserva un horario existente para un paciente.
     */
    suspend fun reservarCita(idCita: Long, idUsuario: Long): CitaDto

    /**
     * Obtiene un Flow con todas las citas activas de un paciente.
     */
    fun getAppointmentsForPatient(patientId: Long): Flow<List<CitaDetalleDto>>

    /**
     * Obtiene las citas de un paciente en una sola consulta.
     */
    suspend fun getAppointmentsForPatientOnce(patientId: Long): Result<List<CitaDto>>

    /**
     * Obtiene las citas actuales de un doctor.
     */
    suspend fun getAppointmentsForDoctorOnce(doctorId: Long): Result<List<CitaDto>>

    /**
     * Obtiene las prИximas citas de un paciente (puede filtrar localmente).
     */
    suspend fun getUpcomingAppointmentsForPatient(patientId: Long): Result<List<CitaDto>>

    /**
     * Cancela una cita.
     */
    suspend fun cancelAppointment(appointmentId: Long): Result<Unit>

    /**
     * Obtiene las prИximas citas de un paciente directamente.
     */
    suspend fun getProximasCitasByUsuario(userId: Long): List<CitaDto>

    /**
     * Obtiene las prИximas citas de un doctor directamente.
     */
    suspend fun getProximasCitasDoctor(doctorId: Long): List<CitaDto>

    /**
     * Obtiene las prИximas citas de un paciente que pertenecen a un doctor determinado.
     */
    suspend fun getProximasCitasPacienteConDoctor(pacienteId: Long, doctorId: Long): List<CitaDto>

    /**
     * Cancela una cita usando el nuevo endpoint.
     */
    suspend fun cancelarCita(citaId: Long): Result<Unit>

    /**
     * Marca una cita como realizada.
     */
    suspend fun finalizarCita(citaId: Long): Result<CitaDto>
}
