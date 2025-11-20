package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.CitasApi
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.dto.CitaDetalleDto
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * Implementación del repositorio de Citas basada en Retrofit.
 */
class CitasRepositoryImpl(
    private val citasApi: CitasApi = RetrofitClient.citasApi
) : CitasRepository {

    override suspend fun bookAppointment(appointment: CitaDto): Result<CitaDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val created = citasApi.createAppointment(appointment)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookedTimes(doctorId: Long, date: String): Result<List<String>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val bookedTimes = citasApi.getAppointments()
                .filter { it.doctorId == doctorId && it.date == date }
                .map { it.time }
            Result.success(bookedTimes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAppointmentsForPatient(patientId: Long): Flow<List<CitaDetalleDto>> = flow {
        val appointments = withContext(Dispatchers.IO) {
            citasApi.getAppointments()
                .filter { it.patientId == patientId }
                .map { it.toDetalleDto() }
        }
        emit(appointments)
    }

    override suspend fun getAppointmentsForPatientOnce(patientId: Long): Result<List<CitaDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val appointments = citasApi.getAppointments().filter { it.patientId == patientId }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAppointment(appointmentId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            citasApi.deleteAppointment(appointmentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
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
