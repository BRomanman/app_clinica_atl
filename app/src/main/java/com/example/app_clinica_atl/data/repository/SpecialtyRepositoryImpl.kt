package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SpecialtyRepositoryImpl(
    private val api: UsuariosApi = RetrofitClient.usuariosApi
) : SpecialtyRepository {

    override fun getAllSpecialties(): Flow<List<EspecialidadDto>> = flow {
        val resp = api.getAllSpecialties()
        if (!resp.isSuccessful) throw HttpException(resp)
        val list: List<EspecialidadResponseDto> = resp.body() ?: emptyList()
        emit(list.mapNotNull { it.toUiOrNull() })
    }

    override suspend fun createSpecialty(body: EspecialidadRequestDto): Result<EspecialidadDto> =
        withContext(Dispatchers.IO) {
            runCatching<EspecialidadDto> {
                val resp = api.createSpecialty(body)
                if (!resp.isSuccessful) throw HttpException(resp)
                val created: EspecialidadResponseDto =
                    resp.body() ?: error("Respuesta vacía al crear especialidad")
                created.toUiOrNull() ?: error("Especialidad creada sin nombre")
            }
        }

    private fun EspecialidadResponseDto.toUiOrNull(): EspecialidadDto? {
        val cleanName = nombre?.trim().orEmpty()
        if (cleanName.isBlank()) return null

        return EspecialidadDto(
            id = this.id ?: 0L,
            name = cleanName,
            price = 0.0
        )
    }
}
