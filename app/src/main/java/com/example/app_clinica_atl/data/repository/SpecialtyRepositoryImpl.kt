package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class SpecialtyRepositoryImpl : SpecialtyRepository {

    private val specialties = MutableStateFlow<List<EspecialidadDto>>(emptyList())

    override fun getAllSpecialties(): Flow<List<EspecialidadDto>> = specialties.asStateFlow()

    override suspend fun addSpecialty(specialty: EspecialidadDto): Result<Unit> {
        return try {
            if (specialty.name.isBlank()) {
                throw IllegalArgumentException("El nombre no puede estar vacío.")
            }
            if (specialty.price <= 0) {
                throw IllegalArgumentException("El precio debe ser mayor a 0.")
            }
            val updated = specialties.value + specialty
            specialties.value = updated
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSpecialty(specialty: EspecialidadDto): Result<Unit> {
        return try {
            specialties.value = specialties.value.filterNot { it.id == specialty.id }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
