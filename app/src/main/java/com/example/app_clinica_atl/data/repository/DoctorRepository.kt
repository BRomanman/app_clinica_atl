package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.dto.UsuarioDto

/**
 * Interfaz para el repositorio de Doctores.
 * Ahora funciona con DTOs en lugar de Entities de Room.
 */
interface DoctorRepository {
    /**
     * Obtiene una lista de doctores filtrados por especialidad.
     */
    suspend fun getDoctorsBySpecialty(specialty: String): Result<List<UsuarioDto>>

    /**
     * Obtiene un doctor específico por su ID.
     */
    suspend fun getDoctorById(id: Long): Result<UsuarioDto>
}
