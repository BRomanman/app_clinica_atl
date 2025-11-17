package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.usuario.UsuarioDao
import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity
import java.util.NoSuchElementException

/**
 * Implementación del repositorio de Doctores.
 * NO usa Hilt. Recibe sus dependencias (UsuarioDao) manualmente.
 */
class DoctorRepositoryImpl(
    private val userDao: UsuarioDao // <-- Recibe el DAO
) : DoctorRepository {

    /**
     * Obtiene doctores reales de la base de datos usando el DAO.
     */
    override suspend fun getDoctorsBySpecialty(specialty: String): Result<List<UsuarioEntity>> {
        return try {
            val doctors = userDao.getDoctorsBySpecialty(specialty)
            Result.success(doctors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene un doctor real por su ID.
     */
    override suspend fun getDoctorById(id: Long): Result<UsuarioEntity> {
        return try {
            val doctor = userDao.getById(id)
            if (doctor != null) {
                Result.success(doctor)
            } else {
                throw NoSuchElementException("Doctor no encontrado con ID $id")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}