package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.appointment.AppointmentDao
import com.example.app_clinica_atl.data.local.appointment.AppointmentDetails
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
import kotlinx.coroutines.flow.Flow
import java.io.IOException

/**
 * Implementación del repositorio de Citas.
 */
class AppointmentRepositoryImpl(
    private val appointmentDao: AppointmentDao
) : AppointmentRepository {

    override suspend fun bookAppointment(appointment: AppointmentEntity): Result<Long> {
        return try {
            val existingAppointment = appointmentDao.getAppointmentByDoctorDateTime(
                appointment.doctorId,
                appointment.date,
                appointment.time
            )
            if (existingAppointment != null) {
                throw IllegalStateException("La hora seleccionada ya no está disponible.")
            }
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

    // --- ¡¡FUNCIONES AÑADIDAS!! ---

    override fun getAppointmentsForPatient(patientId: Long): Flow<List<AppointmentDetails>> {
        // Simplemente pasa la llamada al DAO.
        // El 'catch' de errores se hará en el ViewModel que colecciona este Flow.
        return appointmentDao.getActiveAppointmentsForPatient(patientId)
    }

    override suspend fun cancelAppointment(appointmentId: Long): Result<Unit> {
        return try {
            appointmentDao.cancelAppointment(appointmentId)
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}