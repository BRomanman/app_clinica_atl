package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.CitasApi
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.dto.CitaDetalleDto
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Implementación del repositorio de Citas basada en Retrofit.
 */
class CitasRepositoryImpl(
    private val citasApi: CitasApi = RetrofitClient.citasApi
) : CitasRepository {

    override suspend fun bookAppointment(appointment: CitaDto): Result<CitaDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = citasApi.createAppointment(appointment)
            val created = response.bodyOrThrow("Cuerpo vacío al crear la cita.")
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookedTimes(doctorId: Long, date: String): Result<List<String>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val bookedTimes = citasApi.getAppointmentsByDoctorAndDate(doctorId, date)
                .bodyOrEmpty()
                // Solo consideramos como tomadas las citas no disponibles o con estado distinto de "Disponible"
                .filter { it.available == false || !it.status.equals("Disponible", ignoreCase = true) }
                .map { it.time }
            Result.success(bookedTimes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAppointmentsForPatient(patientId: Long): Flow<List<CitaDetalleDto>> = flow {
        val appointments = withContext(Dispatchers.IO) {
            citasApi.getAppointmentsByUser(patientId)
                .bodyOrEmpty()
                .map { it.toDetalleDto() }
        }
        emit(appointments)
    }

    override suspend fun getAppointmentsForPatientOnce(patientId: Long): Result<List<CitaDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val appointments = citasApi.getAppointmentsByUser(patientId).bodyOrEmpty()
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentsForDoctorOnce(doctorId: Long): Result<List<CitaDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val appointments = citasApi.getUpcomingAppointmentsByDoctor(doctorId)
                .bodyOrEmpty()
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingAppointmentsForPatient(patientId: Long): Result<List<CitaDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val appointments = citasApi.getUpcomingAppointmentsByUser(patientId).bodyOrEmpty()
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAppointment(appointmentId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val current = citasApi.getAppointmentById(appointmentId).bodyOrThrow("Cita no encontrada.")
            val updateResponse = citasApi.updateAppointment(
                appointmentId,
                current.copy(status = "Cancelada", available = false)
            )
            if (updateResponse.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(updateResponse))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun <T> retrofit2.Response<List<T>>.bodyOrEmpty(): List<T> {
    if (isSuccessful) return body().orEmpty()
    throw HttpException(this)
}

private fun <T> retrofit2.Response<T>.bodyOrThrow(emptyMessage: String): T {
    if (isSuccessful) {
        return body() ?: throw IllegalStateException(emptyMessage)
    }
    throw HttpException(this)
}

private fun CitaDto.toDetalleDto(): CitaDetalleDto {
    // TODO: Enriquecer con datos reales del doctor cuando el backend exponga ese join.
    return CitaDetalleDto(
        appointmentId = id,
        doctorName = "Doctor #$doctorId",
        doctorSpecialty = "",
        date = date,
        time = time,
        status = status
    )
}
