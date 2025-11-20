package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.HistorialesApi
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistorialRepository(
    private val historialesApi: HistorialesApi = RetrofitClient.historialesApi
) {
    suspend fun getHistorialForUser(userId: Long): Result<List<HistorialDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val histories = historialesApi.getHistorialByUserId(userId)
            Result.success(histories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
