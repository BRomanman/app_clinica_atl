package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import kotlinx.coroutines.flow.Flow

interface SpecialtyRepository {
    fun getAllSpecialties(): Flow<List<EspecialidadDto>>
    suspend fun createSpecialty(body: EspecialidadRequestDto): Result<EspecialidadDto>
}
