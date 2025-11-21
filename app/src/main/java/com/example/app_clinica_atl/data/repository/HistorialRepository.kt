package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.HistorialesApi
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class HistorialRepository(
    private val historialesApi: HistorialesApi = RetrofitClient.historialesApi
) {
    suspend fun getHistorialForUser(userId: Long): Result<List<HistorialDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val histories = try {
                historialesApi.getHistorialByUserId(userId)
            } catch (e: HttpException) {
                // Si el backend responde 404, lo interpretamos como "sin historial".
                if (e.code() == 404) emptyList() else throw e
            }
            Result.success(histories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistorialForDoctor(doctorId: Long): Result<List<HistorialDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val histories = try {
                historialesApi.getHistorialByDoctorId(doctorId)
            } catch (e: HttpException) {
                if (e.code() == 404 || e.code() == 204) emptyList() else throw e
            }
            Result.success(histories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
