package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity

/**
 * Interfaz para el repositorio de Doctores.
 * Ahora devuelve nuestra entidad real 'UsuarioEntity' envuelta en 'Result'.
 */
interface DoctorRepository {
    /**
     * Obtiene una lista de doctores filtrados por especialidad.
     */
    suspend fun getDoctorsBySpecialty(specialty: String): Result<List<UsuarioEntity>>

    /**
     * Obtiene un doctor específico por su ID.
     */
    suspend fun getDoctorById(id: Long): Result<UsuarioEntity>
}