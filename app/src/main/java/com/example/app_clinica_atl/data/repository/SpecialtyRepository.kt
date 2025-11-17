package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.especialidad.EspecialidadEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio de Especialidades.
 */
interface SpecialtyRepository {
    /**
     * Obtiene un Flow con la lista de todas las especialidades.
     */
    fun getAllSpecialties(): Flow<List<EspecialidadEntity>>

    /**
     * Añade una nueva especialidad a la base de datos.
     */
    suspend fun addSpecialty(specialty: EspecialidadEntity): Result<Unit>

    /**
     * Elimina una especialidad de la base de datos.
     */
    suspend fun deleteSpecialty(specialty: EspecialidadEntity): Result<Unit>
}