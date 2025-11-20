package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.remote.dto.toUsuarioDto
import java.util.NoSuchElementException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación del repositorio de Doctores basada en DTOs remotos.
 */
class DoctorRepositoryImpl(
    private val usuariosApi: UsuariosApi = RetrofitClient.usuariosApi
) : DoctorRepository {

    override suspend fun getDoctorsBySpecialty(specialty: String): Result<List<UsuarioDto>> = withContext(Dispatchers.IO) {
        try {
            val doctors = usuariosApi.getUsers()
                .map { it.toUsuarioDto() }
                .filter { it.role.equals("doctor", true) }

            // TODO: Reemplazar por filtro real cuando la API exponga la especialidad del doctor.
            val filtered = if (specialty.isBlank()) doctors else doctors.filter {
                it.specialty?.contains(specialty, true) == true
            }

            Result.success(filtered.ifEmpty { doctors })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDoctorById(id: Long): Result<UsuarioDto> = withContext(Dispatchers.IO) {
        try {
            val doctor = usuariosApi.getUserById(id).toUsuarioDto()
            if (doctor.role.equals("doctor", true)) {
                Result.success(doctor)
            } else {
                Result.failure(NoSuchElementException("El usuario $id no es un doctor."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
