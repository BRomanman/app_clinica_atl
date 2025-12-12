package com.example.app_clinica_atl.data.repository

import android.util.Log
import com.example.app_clinica_atl.data.remote.HistorialesApi
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

private const val TAG = "HistorialRepository"

class HistorialRepository(
    private val historialesApi: HistorialesApi = RetrofitClient.historialesApi
) {
    suspend fun getHistorialForUser(userId: Long): Result<List<HistorialDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Solicitando historial para usuario $userId")
            val histories = historialesApi.getHistorialByUserId(userId).toBodyOrEmpty()
            Result.success(
                histories.filter { it.idUsuario == userId || it.idDoctor == userId }
                    .filterNot { it.estado?.contains("cancel", ignoreCase = true) == true }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistorialForDoctor(doctorId: Long): Result<List<HistorialDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Solicitando historial para doctor $doctorId")
            val histories = historialesApi.getHistorialByDoctorId(doctorId).toBodyOrEmpty()
                .filter { it.idDoctor == doctorId }
                .filterNot { it.estado?.contains("cancel", ignoreCase = true) == true }
            Result.success(histories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun retrofit2.Response<List<HistorialDto>>.toBodyOrEmpty(): List<HistorialDto> {
        if (isSuccessful) return body().orEmpty()
        if (code() == 204 || code() == 404) return emptyList()
        throw HttpException(this)
    }
}
