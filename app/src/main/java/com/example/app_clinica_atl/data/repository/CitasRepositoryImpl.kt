package com.example.app_clinica_atl.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.app_clinica_atl.data.remote.CitasApi
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.remote.dto.CitaDetalleDto
import com.example.app_clinica_atl.data.remote.dto.ReservarCitaRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CitasRepositoryImpl(
    private val citasApi: CitasApi = RetrofitClient.citasApi
) : CitasRepository {

    override suspend fun getCitasUsuario(idUsuario: Long): List<CitaDto> = withContext(Dispatchers.IO) {
        citasApi.getCitasByUsuario(idUsuario).bodyOrEmpty()
    }

    override suspend fun getHorariosDisponibles(doctorId: Long, date: String): List<CitaDto> =
        withContext(Dispatchers.IO) {
            val response = citasApi.getCitasPorDoctorYFecha(doctorId, date)
            if (!response.isSuccessful) throw HttpException(response)
            response.body()
                .orEmpty()
                .filter { it.isOpenSlot() }
        }

    override suspend fun reservarCita(idCita: Long, idUsuario: Long): CitaDto = withContext(Dispatchers.IO) {
        val response = citasApi.reservarCita(idCita, ReservarCitaRequest(idUsuario = idUsuario))
        if (response.isSuccessful) {
            return@withContext response.body()
                ?: throw IllegalStateException("No se recibió la información de la cita confirmada.")
        }
        when (response.code()) {
            409 -> throw SlotAlreadyTakenException()
            404 -> throw CitaNotFoundException()
            405 -> throw HttpException(response) // metodo no permitido
            else -> throw HttpException(response)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAppointmentsForPatient(patientId: Long): Flow<List<CitaDetalleDto>> = flow {
        try {
            val appointments = withContext(Dispatchers.IO) {
                citasApi.getCitasByUsuario(patientId).bodyOrEmpty()
            }

            val doctorCache = mutableMapOf<Long, DoctorBrief>()
            val mapped = appointments
                .filter { cita -> !cita.status.contains("cancel", ignoreCase = true) && !cita.status.equals("disponible", ignoreCase = true) }
                .sortedBy { parseDateTime(it) ?: LocalDateTime.MAX }
                .map { cita ->
                    val doctorInfo = doctorCache.getOrPut(cita.doctorId) { fetchDoctorBrief(cita.doctorId) }
                    cita.toDetalleDto(
                        doctorName = doctorInfo.name,
                        doctorSpecialty = doctorInfo.specialty
                    )
                }

            emit(mapped)
        } catch (e: Exception) {
            println("Error al obtener las citas. Error: ${e.message}")
            emit(emptyList())
        }
    }

    override suspend fun getAppointmentsForPatientOnce(patientId: Long): Result<List<CitaDto>> = withContext(Dispatchers.IO) {
        try {
            val appointments = citasApi.getCitasByUsuario(patientId).bodyOrEmpty()
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentsForDoctorOnce(doctorId: Long): Result<List<CitaDto>> = withContext(Dispatchers.IO) {
        try {
            val appointments = citasApi.getProximasCitasDoctor(doctorId).bodyOrEmpty()
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingAppointmentsForPatient(patientId: Long): Result<List<CitaDto>> = withContext(Dispatchers.IO) {
        try {
            val appointments = citasApi.getProximasCitasUsuario(patientId).bodyOrEmpty()
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProximasCitasDoctor(doctorId: Long): List<CitaDto> = withContext(Dispatchers.IO) {
        citasApi.getProximasCitasDoctor(doctorId).bodyOrEmpty()
    }

    override suspend fun getProximasCitasPacienteConDoctor(pacienteId: Long, doctorId: Long): List<CitaDto> =
        withContext(Dispatchers.IO) {
            citasApi.getProximasCitasUsuario(pacienteId).bodyOrEmpty()
                .filter { it.doctorId == doctorId }
        }

    override suspend fun cancelAppointment(appointmentId: Long): Result<Unit> {
        return cancelarCita(appointmentId)
    }

    override suspend fun getProximasCitasByUsuario(userId: Long): List<CitaDto> = withContext(Dispatchers.IO) {
        citasApi.getProximasCitasUsuario(userId).bodyOrEmpty()
    }

    override suspend fun cancelarCita(citaId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = citasApi.cancelarCita(citaId)
            if (!response.isSuccessful) throw HttpException(response)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun finalizarCita(citaId: Long): Result<CitaDto> = withContext(Dispatchers.IO) {
        try {
            val current = citasApi.getCitaById(citaId)
            val updated = citasApi.updateCita(citaId, current.copy(estado = "REALIZADA"))
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

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

private fun CitaDto.isOpenSlot(): Boolean {
    return available || status.equals("Disponible", ignoreCase = true)
 }

private fun retrofit2.Response<List<CitaDto>>.bodyOrEmpty(): List<CitaDto> {
    if (isSuccessful) return body().orEmpty()
    if (code() == 204 || code() == 404) return emptyList()
    throw HttpException(this)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun parseDateTime(cita: CitaDto): LocalDateTime? {
    val datePart = cita.date.ifBlank { "" }
    if (datePart.isBlank()) return null

    val start = cita.startTime.ifBlank { cita.time }
    val parsedDate = runCatching { LocalDate.parse(datePart) }.getOrNull() ?: return null
    val parsedTime = runCatching { LocalTime.parse(start) }.getOrDefault(LocalTime.MIDNIGHT)
    return LocalDateTime.of(parsedDate, parsedTime)
}

private data class DoctorBrief(val name: String, val specialty: String)

private suspend fun fetchDoctorBrief(doctorId: Long): DoctorBrief = withContext(Dispatchers.IO) {
    runCatching {
        val response = RetrofitClient.usuariosApi.getDocById(doctorId)
        val doctorDto = response.body() ?: return@withContext DoctorBrief(
            name = "Doctor #$doctorId",
            specialty = ""
        )
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
