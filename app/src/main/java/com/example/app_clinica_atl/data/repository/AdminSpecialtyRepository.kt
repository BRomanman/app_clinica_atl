package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadUpdateRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository específico para la administración de especialidades.
 *
 * ⚠ Este repository NO reemplaza al que ya usas en otras partes,
 *    es sólo para pantallas de administración (AdminManageSpecialtiesScreen).
 */
class AdminSpecialtyRepository(
    private val api: UsuariosApi
) {

    /**
     * Obtiene todas las especialidades desde la API.
     *
     * GET /api/v1/especialidades
     */
    suspend fun getAllSpecialties(): Result<List<EspecialidadResponseDto>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getAllSpecialties()
                if (response.isSuccessful) {
                    val body = response.body().orEmpty()
                    Result.success(body)
                } else {
                    Result.failure(HttpException(response))
                }
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Actualiza solo el nombre de una especialidad.
     *
     * PUT /api/v1/especialidades/{id}
     * Enviamos doctorId = null para no cambiar la relación con el doctor.
     */
    suspend fun updateSpecialtyName(
        id: Long,
        newName: String
    ): Result<EspecialidadResponseDto> =
        withContext(Dispatchers.IO) {
            try {
                val request = EspecialidadUpdateRequestDto(
                    nombre = newName,
                    doctorId = null
                )
                val response = api.updateSpecialty(id, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(IllegalStateException("Respuesta vacía del servidor"))
                    }
                } else {
                    Result.failure(HttpException(response))
                }
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
