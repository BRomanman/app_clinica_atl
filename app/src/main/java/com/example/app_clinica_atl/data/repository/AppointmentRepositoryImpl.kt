package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.appointment.AppointmentDao
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity

/**
 * Implementación del repositorio de Citas.
 * Recibe el DAO manualmente y ejecuta las consultas.
 */
class AppointmentRepositoryImpl(
    private val appointmentDao: AppointmentDao // <-- Recibe el DAO real
) : AppointmentRepository {

    override suspend fun bookAppointment(appointment: AppointmentEntity): Result<Long> {
        return try {
            // Lógica para evitar duplicados (opcional pero recomendado)
            val existingAppointment = appointmentDao.getAppointmentByDoctorDateTime(
                appointment.doctorId,
                appointment.date,
                appointment.time
            )
            if (existingAppointment != null) {
                throw IllegalStateException("La hora seleccionada ya no está disponible.")
            }
            // Inserta la nueva cita
            val newId = appointmentDao.insert(appointment)
            Result.success(newId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookedTimes(doctorId: Long, date: String): Result<List<String>> {
        return try {
            val bookedTimes = appointmentDao.getBookedTimesForDoctorOnDate(doctorId, date)
            Result.success(bookedTimes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}