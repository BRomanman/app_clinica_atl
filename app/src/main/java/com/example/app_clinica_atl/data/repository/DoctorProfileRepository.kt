package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.CitasApi
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.CitaDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import retrofit2.HttpException

class DoctorProfileRepository(
    private val usuariosApi: UsuariosApi = RetrofitClient.usuariosApi,
    private val citasApi: CitasApi = RetrofitClient.citasApi
) {

    suspend fun getDoctorProfile(doctorId: Long): Result<DoctorDto> {
        return try {
            Result.success(usuariosApi.getDocById(doctorId))
        } catch (e: HttpException) {
            val message = when (e.code()) {
                404 -> "Doctor no encontrado."
                else -> e.message()
            }
            Result.failure(Exception(message ?: "Error HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAppointmentsForDoctor(doctorId: Long): Result<List<CitaDto>> {
        return try {
            Result.success(citasApi.getProximasCitasDoctor(doctorId))
        } catch (e: HttpException) {
            if (e.code() == 204) {
                Result.success(emptyList())
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSpecialtiesForDoctor(doctorId: Long): Result<List<EspecialidadResponseDto>> {
        return try {
            val list = usuariosApi.getDoctorSpecialties(doctorId)
            Result.success(list)
        } catch (e: HttpException) {
            if (e.code() == 204) Result.success(emptyList()) else Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
