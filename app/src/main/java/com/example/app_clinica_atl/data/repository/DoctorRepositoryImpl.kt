package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.remote.dto.toUsuarioDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.NoSuchElementException

/**
 * Repositorio de Doctores (solo capa remota).
 * Fixes:
 *  - Sin usar 'doctorId' en EspecialidadResponseDto (no existe).
 *  - Se consulta /doctores/{id}/especialidades por cada doctor.
 *  - runCatching tipado explícito para evitar inferencia.
 */
class DoctorRepositoryImpl(
    private val usuariosApi: UsuariosApi = RetrofitClient.usuariosApi
) : DoctorRepository {

    override suspend fun getDoctorsBySpecialty(specialty: String): Result<List<UsuarioDto>> =
        withContext(Dispatchers.IO) {
            try {
                val target = specialty.trim()

                // 1) Traer todos los doctores desde /doctores (contiene usuario dentro)
                val allDoctors: List<DoctorDto> = runCatching<List<DoctorDto>> {
                    usuariosApi.getDoc()
                }.getOrElse { emptyList() }

                // Si no se pidió filtro, devolver todos los usuarios con rol doctor
                if (target.isBlank()) {
                    val mapped: List<UsuarioDto> = allDoctors.mapNotNull { d ->
                        d.usuario?.toUsuarioDto()
                    }.filter { it.role.equals("doctor", ignoreCase = true) }

                    return@withContext Result.success(mapped)
                }

                // 2) Con filtro por nombre de especialidad:
                val result = mutableListOf<UsuarioDto>()

                for (doc in allDoctors) {
                    val docId: Long = doc.id ?: continue

                    // /doctores/{doctorId}/especialidades
                    val specials: List<EspecialidadResponseDto> =
                        runCatching<List<EspecialidadResponseDto>> {
                            usuariosApi.getDoctorSpecialties(docId)
                        }.getOrElse { emptyList() }

                    val match: EspecialidadResponseDto? =
                        specials.firstOrNull { it.nombre.equals(target, ignoreCase = true) }

                    if (match != null && doc.usuario != null) {
                        val user = doc.usuario.toUsuarioDto()
                        if (user.role.equals("doctor", ignoreCase = true)) {
                            result += user.copy(specialty = match.nombre)
                        }
                    }
                }

                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getDoctorById(id: Long): Result<UsuarioDto> =
        withContext(Dispatchers.IO) {
            try {
                val user = usuariosApi.getUserById(id).toUsuarioDto()
                if (user.role.equals("doctor", ignoreCase = true)) {
                    Result.success(user)
                } else {
                    Result.failure(NoSuchElementException("El usuario $id no es un doctor."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
