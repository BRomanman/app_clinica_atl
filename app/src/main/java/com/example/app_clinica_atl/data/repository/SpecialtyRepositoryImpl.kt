package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.especialidad.EspecialidadDao
import com.example.app_clinica_atl.data.local.especialidad.EspecialidadEntity
import kotlinx.coroutines.flow.Flow
import java.io.IOException

/**
 * Implementación del repositorio de Especialidades.
 * Recibe el DAO manualmente.
 */
class SpecialtyRepositoryImpl(
    private val specialtyDao: EspecialidadDao
) : SpecialtyRepository {

    override fun getAllSpecialties(): Flow<List<EspecialidadEntity>> {
        return specialtyDao.getAllSpecialties()
    }

    override suspend fun addSpecialty(specialty: EspecialidadEntity): Result<Unit> {
        return try {
            // Validaciones de lógica de negocio
            if (specialty.name.isBlank()) {
                throw IllegalArgumentException("El nombre no puede estar vacío.")
            }
            if (specialty.price <= 0) {
                throw IllegalArgumentException("El precio debe ser mayor a 0.")
            }

            specialtyDao.insert(specialty)
            Result.success(Unit)
        } catch (e: Exception) {
            // Captura errores, incluyendo el de "Nombre duplicado"
            Result.failure(e)
        }
    }

    override suspend fun deleteSpecialty(specialty: EspecialidadEntity): Result<Unit> {
        return try {
            specialtyDao.delete(specialty)
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}