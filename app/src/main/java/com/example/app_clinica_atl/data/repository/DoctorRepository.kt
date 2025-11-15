package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.user.UserEntity

/**
 * Interfaz para el repositorio de Doctores.
 * Ahora devuelve nuestra entidad real 'UserEntity' envuelta en 'Result'.
 */
interface DoctorRepository {
    /**
     * Obtiene una lista de doctores filtrados por especialidad.
     */
    suspend fun getDoctorsBySpecialty(specialty: String): Result<List<UserEntity>>

    /**
     * Obtiene un doctor específico por su ID.
     */
    suspend fun getDoctorById(id: Long): Result<UserEntity>
}