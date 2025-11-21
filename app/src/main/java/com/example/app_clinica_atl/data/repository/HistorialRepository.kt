package com.example.app_clinica_atl.data.repository

import android.util.Log
import com.example.app_clinica_atl.data.remote.HistorialesApi
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

private const val TAG = "HistorialRepository"

class HistorialRepository(
    private val historialesApi: HistorialesApi = RetrofitClient.historialesApi,
    private val gson: Gson = Gson()
) {
    suspend fun getHistorialForUser(userId: Long): Result<List<HistorialDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Solicitando historial para usuario $userId")
            val histories = fetchAndParse(historialesApi.getHistorialByUserId(userId))
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
            val histories = fetchAndParse(historialesApi.getHistorialByDoctorId(doctorId))
                .filter { it.idDoctor == doctorId }
                .filterNot { it.estado?.contains("cancel", ignoreCase = true) == true }
            Result.success(histories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun fetchAndParse(response: Response<ResponseBody>): List<HistorialDto> {
        if (response.isSuccessful) return response.parseListOrEmpty()
        if (response.code() == 204 || response.code() == 404) return emptyList()
        throw HttpException(response)
    }

    private fun Response<ResponseBody>.parseListOrEmpty(): List<HistorialDto> {
        val bodyString = try {
            body()?.string()
        } catch (e: Exception) {
            null
        }
        if (bodyString.isNullOrBlank()) return emptyList()
        return runCatching {
            val type = object : TypeToken<List<HistorialDto>>() {}.type
            gson.fromJson<List<HistorialDto>>(bodyString, type)
        }.getOrElse {
            Log.w(TAG, "No se pudo parsear historial, devolviendo vacío: ${it.message}")
            emptyList()
        }
    }
}
