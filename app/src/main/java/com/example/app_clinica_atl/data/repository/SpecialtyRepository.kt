package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio de Especialidades.
 */
interface SpecialtyRepository {
    /**
     * Obtiene un Flow con la lista de todas las especialidades.
     */
    fun getAllSpecialties(): Flow<List<EspecialidadDto>>

    /**
     * Añade una nueva especialidad.
     */
    suspend fun addSpecialty(specialty: EspecialidadDto): Result<Unit>

    /**
     * Elimina una especialidad.
     */
    suspend fun deleteSpecialty(specialty: EspecialidadDto): Result<Unit>
}
