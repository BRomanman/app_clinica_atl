package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.remote.dto.toUsuarioDto
import java.util.NoSuchElementException
import retrofit2.HttpException
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
            val specialtiesResponse = usuariosApi.getAllSpecialties()
            val specialties = if (specialtiesResponse.isSuccessful) {
                specialtiesResponse.body().orEmpty()
            } else {
                throw HttpException(specialtiesResponse)
            }
            val targetName = specialty.trim()

            // Si no se seleccionó especialidad, devolvemos todos los doctores.
            if (targetName.isBlank()) {
                val allDoctors = usuariosApi.getUsers()
                    .map { it.toUsuarioDto() }
                    .filter { it.role.equals("doctor", true) }
                return@withContext Result.success(allDoctors)
            }

            val matching = specialties.filter { it.nombre.equals(targetName, true) }
            val doctorIds = matching.mapNotNull { it.doctorId }.distinct()

            // Si no hay doctores asociados a esa especialidad, devolvemos lista vacía
            if (doctorIds.isEmpty()) return@withContext Result.success(emptyList())

            val doctors = doctorIds.mapNotNull { doctorId ->
                val doctorDto = runCatching { usuariosApi.getDocById(doctorId) }.getOrNull()
                val userDto = doctorDto?.usuario

                val baseUser = userDto?.toUsuarioDto() ?: runCatching {
                    usuariosApi.getUserById(doctorId).toUsuarioDto()
                }.getOrNull()

                baseUser?.copy(
                    specialty = doctorDto?.especialidad
                        ?: matching.firstOrNull { it.doctorId == doctorId }?.nombre
                )
            }.filter { it.role.equals("doctor", true) }

            Result.success(doctors)
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
