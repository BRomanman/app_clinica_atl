package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity
import java.util.NoSuchElementException

/**
 * Implementación del repositorio de Doctores.
 * NO usa Hilt. Recibe sus dependencias (UserDao) manualmente.
 */
class DoctorRepositoryImpl(
    private val userDao: UserDao // <-- Recibe el DAO
) : DoctorRepository {

    /**
     * Obtiene doctores reales de la base de datos usando el DAO.
     */
    override suspend fun getDoctorsBySpecialty(specialty: String): Result<List<UserEntity>> {
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
    override suspend fun getDoctorById(id: Long): Result<UserEntity> {
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