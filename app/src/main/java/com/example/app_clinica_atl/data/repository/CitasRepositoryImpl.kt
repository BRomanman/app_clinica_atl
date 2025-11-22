package com.example.app_clinica_atl.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.app_clinica_atl.data.remote.CitasApi
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.RetrofitClient.usuariosApi
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.CitaDetalleDto
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Implementación del repositorio de Citas basada en Retrofit.
 */
class CitasRepositoryImpl(
    private val citasApi: CitasApi = RetrofitClient.citasApi,
    private val usuariosApi: UsuariosApi = RetrofitClient.usuariosApi
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAppointmentsForPatient(patientId: Long): Flow<List<CitaDetalleDto>> = flow {
        val doctorCache = mutableMapOf<Long, DoctorBrief>()

        val appointments = withContext(Dispatchers.IO) {
            citasApi.getAppointmentsByUser(patientId).bodyOrEmpty()
        }

        val mapped = appointments
            .sortedBy { parseDateTime(it) ?: LocalDateTime.MAX }
            .map { cita ->
                val doctorInfo = doctorCache.getOrPut(cita.doctorId) { fetchDoctorBrief(cita.doctorId) }
                cita.toDetalleDto(
                    doctorName = doctorInfo.name,
                    doctorSpecialty = doctorInfo.specialty
                )
            }

        emit(mapped)
    }

    override suspend fun getAppointmentsForPatientOnce(patientId: Long): Result<List<CitaDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val appointments = citasApi.getAppointmentsByUser(patientId).bodyOrEmpty()
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableSlots(doctorId: Long, date: String): Result<List<String>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = citasApi.getAppointmentsByDoctorAndDate(doctorId, date)
            val slots = when {
                response.code() == 204 -> emptyList()
                response.isSuccessful -> response.body().orEmpty()
                else -> throw HttpException(response)
            }
                .filter { cita ->
                    cita.available &&
                            cita.status.equals("DISPONIBLE", true) &&
                            cita.date.equals(date, true)
                }
                .map { it.startTime.ifBlank { it.time } }
                .map { time -> time.take(5) }
                .distinct()
                .sorted()
            Result.success(slots)
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

private data class DoctorBrief(val name: String, val specialty: String)

private fun CitaDto.toDetalleDto(doctorName: String, doctorSpecialty: String): CitaDetalleDto {
    return CitaDetalleDto(
        appointmentId = id,
        doctorName = doctorName,
        doctorSpecialty = doctorSpecialty.ifBlank { "Sin especialidad" },
        date = date,
        time = time,
        status = status
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun parseDateTime(cita: CitaDto): LocalDateTime? {
    val datePart = cita.date.ifBlank { cita.dateTime.substringBefore('T', "") }
    if (datePart.isBlank()) return null

    val timePart = cita.time
        .ifBlank { cita.startTime.take(5) }
        .ifBlank { cita.dateTime.substringAfter('T', "").take(5) }
        .ifBlank { "00:00" }

    val parsedDate = runCatching { LocalDate.parse(datePart) }.getOrNull() ?: return null
    val parsedTime = runCatching { LocalTime.parse(timePart) }.getOrDefault(LocalTime.MIDNIGHT)
    return LocalDateTime.of(parsedDate, parsedTime)
}

private suspend fun fetchDoctorBrief(doctorId: Long): DoctorBrief = withContext(Dispatchers.IO) {
    runCatching {
        val doctorDto = usuariosApi.getDocById(doctorId)
        val user = doctorDto.usuario
        val fullName = listOfNotNull(user?.nombre, user?.apellido)
            .joinToString(" ")
            .ifBlank { user?.correo.orEmpty() }
            .ifBlank { "Doctor #$doctorId" }

        DoctorBrief(
            name = fullName,
            specialty = doctorDto.especialidad.orEmpty()
        )
    }.getOrElse {
        DoctorBrief(name = "Doctor #$doctorId", specialty = "")
    }
}
