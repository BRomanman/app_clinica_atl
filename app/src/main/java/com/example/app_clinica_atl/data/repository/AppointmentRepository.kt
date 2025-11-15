package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity

/**
 * Interfaz para el repositorio de Citas.
 * Define las operaciones de datos que la app puede realizar.
 */
interface AppointmentRepository {

    /**
     * Intenta agendar una nueva cita en la base de datos.
     * Devuelve el ID de la nueva cita si tiene éxito.
     */
    suspend fun bookAppointment(appointment: AppointmentEntity): Result<Long>

    /**
     * Obtiene una lista de las horas (Strings) que YA están reservadas
     * para un doctor específico en una fecha específica.
     */
    suspend fun getBookedTimes(doctorId: Long, date: String): Result<List<String>>

    // (Más funciones como 'getAppointmentsForUser' se añadirán aquí más tarde)
}