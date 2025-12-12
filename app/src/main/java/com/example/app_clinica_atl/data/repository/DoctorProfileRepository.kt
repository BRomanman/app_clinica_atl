package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.CitasApi
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import retrofit2.HttpException

class DoctorProfileRepository(
    private val usuariosApi: UsuariosApi = RetrofitClient.usuariosApi,
    private val citasApi: CitasApi = RetrofitClient.citasApi
) {

    suspend fun getDoctorProfile(doctorId: Long): Result<DoctorDto> {
        return try {
            val response = usuariosApi.getDocById(doctorId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: DoctorDto(id = doctorId))
            } else {
                throw HttpException(response)
            }
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
            val response = citasApi.getProximasCitasDoctor(doctorId)
            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else if (response.code() == 204) {
                Result.success(emptyList())
            } else {
                throw HttpException(response)
            }
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
            val response = usuariosApi.getDoctorSpecialties(doctorId)
            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else if (response.code() == 204) {
                Result.success(emptyList())
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            if (e.code() == 204) Result.success(emptyList()) else Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
