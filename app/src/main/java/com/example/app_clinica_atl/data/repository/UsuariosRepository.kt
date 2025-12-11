package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.DoctorCreateRequestDto // <-- IMPORT AÑADIDO
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.LoginRequestDto
import com.example.app_clinica_atl.data.remote.dto.RolRequest
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioIdRefDto // <-- IMPORT AÑADIDO
import com.example.app_clinica_atl.data.remote.dto.UsuarioResponseDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.roleToId
import com.example.app_clinica_atl.data.remote.dto.toUsuarioDto
import com.example.app_clinica_atl.data.remote.dto.toUsuarioDtoFromLogin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class UsuariosRepository(
    private val userPreferences: UserPreferences,
    private val usuariosApi: UsuariosApi = RetrofitClient.usuariosApi
) {

    // --- CÓDIGO AÑADIDO ---
    companion object {
        const val DEFAULT_TARIFA_CONSULTA = 25000 // ajusta si quieres otro valor
    }
    // --- FIN DEL CÓDIGO AÑADIDO ---

    suspend fun login(email: String, pass: String): Result<UsuarioDto> = loginViaApi(email, pass)

    /**
     * Registra un paciente (rol fijo = 1). Doctores/administradores se crean en Empleados.
     */
    suspend fun register(newUser: UsuarioDto): Result<UsuarioDto> = withContext(Dispatchers.IO) {
        try {
            val request = newUser.toUpdateRequest(forceRoleId = 1L)
            val created = usuariosApi.createUser(request)
            Result.success(created.toUsuarioDto())
        } catch (e: HttpException) {
            val friendly = when {
                e.code() == 409 -> "El correo ya está registrado."
                (e.message()?.contains("Duplicate entry", true) == true) ||
                        (e.message()?.contains("correo", true) == true) -> "El correo ya está registrado."
                else -> e.message() ?: "Error HTTP ${e.code()}"
            }
            Result.failure(Exception(friendly, e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Long): Result<UsuarioDto> = withContext(Dispatchers.IO) {
        try {
            val dto = usuariosApi.getUserById(id).toUsuarioDto()
            Result.success(dto)
        } catch (e: HttpException) {
            val message = if (e.code() == 404) "Usuario no encontrado." else e.message()
            Result.failure(Exception(message ?: "Error HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserByIdAsFlow(id: Long): Flow<UsuarioDto?> = flow {
        emit(runCatching { usuariosApi.getUserById(id).toUsuarioDto() }.getOrNull())
    }

    suspend fun searchPatients(query: String): Result<List<UsuarioDto>> = withContext(Dispatchers.IO) {
        try {
            val users = usuariosApi.getUsers()
                .map { it.toUsuarioDto() }
                .filter { user ->
                    user.role.equals("paciente", true) &&
                            (user.name.contains(query, true) || user.email.contains(query, true))
                }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- FUNCIÓN CLAVE PARA EL LISTADO DE DOCTORES ---
    fun getAllDoctors(): Flow<List<UsuarioDto>> = flow {
        val doctors = withContext(Dispatchers.IO) {
            runCatching {
                usuariosApi.getDoc().map { it.toUsuarioDto() }
            }.getOrElse {
                usuariosApi.getUsers()
                    .map { it.toUsuarioDto() }
                    .filter { it.role.equals("doctor", true) }
            }
        }
        emit(doctors)
    }

    suspend fun getDoctorIdForUser(userId: Long): Result<Long?> = withContext(Dispatchers.IO) {
        runCatching {
            val user = usuariosApi.getUserById(userId)
            user.doctor?.id ?: user.trabajador?.id
        }
    }

    suspend fun getAllSpecialties(): Result<List<EspecialidadResponseDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = usuariosApi.getAllSpecialties()
            if (response.isSuccessful) response.body().orEmpty() else throw HttpException(response)
        }
    }

    suspend fun loginViaApi(email: String, pass: String): Result<UsuarioDto> = withContext(Dispatchers.IO) {
        try {
            val loginRequest = LoginRequestDto(correo = email, contrasena = pass)
            val loggedUser = usuariosApi.login(loginRequest)
            userPreferences.saveUserSession(
                id = loggedUser.userId,
                role = loggedUser.role,
                doctorId = loggedUser.doctorId,
                nombre = loggedUser.nombre,
                apellido = loggedUser.apellido,
                correo = loggedUser.correo,
                token = loggedUser.token
            )
            Result.success(loggedUser.toUsuarioDtoFromLogin())
        } catch (e: HttpException) {
            val message = if (e.code() == 401) "Credenciales inválidas." else e.message()
            Result.failure(Exception(message ?: "Error HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestPasswordReset(email: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val users = usuariosApi.getUsers()
            val target = users.firstOrNull { it.correo.equals(email, true) }
                ?: return@withContext Result.failure(Exception("No encontramos una cuenta con ese correo."))

            val temporaryPassword = "ATL-${(100000..999999).random()}"
            usuariosApi.updateUser(target.id, UsuarioUpdateRequestDto(contrasena = temporaryPassword))
            Result.success("Hemos enviado instrucciones a $email.")
        } catch (e: HttpException) {
            Result.failure(Exception(e.message() ?: "Error HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfileImageUrl(userId: Long, imageUrl: String): Result<Unit> = Result.success(Unit)

    suspend fun updatePhoneNumber(userId: Long, phone: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            usuariosApi.updateUser(userId, UsuarioUpdateRequestDto(telefono = phone))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(userId: Long, newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            usuariosApi.updateUser(userId, UsuarioUpdateRequestDto(contrasena = newPassword))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(id: Long, updateData: UsuarioUpdateRequestDto): Result<UsuarioDto> = withContext(Dispatchers.IO) {
        try {
            val response = usuariosApi.updateUser(id, updateData)
            Result.success(response.toUsuarioDto())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========= Mantengo para AdminManageSpecialties (solo PUT) =========
    suspend fun createSpecialty(name: String) =
        Result.failure<EspecialidadResponseDto>(IllegalStateException("No se puede crear especialidad sin doctor. Usa 'Crear Doctor'."))
    // ===================================================================

    suspend fun updateSpecialty(id: Long, name: String): Result<EspecialidadResponseDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                val request = EspecialidadUpdateRequestDto(nombre = name)
                val response = usuariosApi.updateSpecialty(id, request)
                if (response.isSuccessful) {
                    response.body() ?: throw IllegalStateException("Respuesta vacía al actualizar especialidad")
                } else {
                    throw HttpException(response)
                }
            }
        }

    // --- CÓDIGO ACTUALIZADO ---
    /**
     * Crea la ficha de doctor para un usuario existente.
     * Envia tarifa_consulta obligatoria (NOT NULL en BD).
     */
    /**
     * Crea un doctor directamente en Empleados (API /doctores).
     */
    suspend fun createDoctor(
        nombre: String,
        apellido: String,
        fechaNacimiento: String,
        correo: String,
        telefono: String,
        contrasena: String,
        idEspecialidad: Long,
        tarifaConsulta: Int,
        sueldo: Long,
        bono: Long?
    ): Result<UsuarioDto> = withContext(Dispatchers.IO) {
        runCatching {
            val doctorPayload = DoctorCreateRequestDto(
                tarifaConsulta = tarifaConsulta,
                sueldo = sueldo,
                bono = bono,
                usuario = UsuarioIdRefDto(id = null),
                nombre = nombre,
                apellido = apellido,
                fechaNacimiento = fechaNacimiento,
                correo = correo,
                telefono = telefono,
                contrasena = contrasena,
                idRol = 2L,
                tipo = "Doctor",
                idEspecialidad = idEspecialidad
            )
            val created = usuariosApi.createDoc(doctorPayload.toDoctorDto())
            created.toUsuarioDto()
        }
    }
    // --- FIN DEL CÓDIGO ACTUALIZADO ---
}

// --- Helper para /api/v1/usuarios ---
private fun UsuarioDto.toUpdateRequest(forceRoleId: Long? = null): UsuarioUpdateRequestDto {
    val parts = name.trim().split(" ", limit = 2)
    val nombre = parts.getOrElse(0) { "" }.ifBlank { "Usuario" }
    val apellidoCalculado = parts.getOrElse(1) { "" }.ifBlank { "Paciente" }
    val apellidoFinal = lastName?.takeIf { it.isNotBlank() } ?: apellidoCalculado
    val roleIdFinal = forceRoleId ?: roleId ?: roleToId(role)
    val birth = birthDate?.takeIf { it.isNotBlank() } ?: "2000-01-01"
    return UsuarioUpdateRequestDto(
        nombre = nombre,
        apellido = apellidoFinal,
        fechaNacimiento = birth,
        correo = email,
        telefono = phone,
        contrasena = password,
        idRol = roleIdFinal,
        rol = RolRequest(id = roleIdFinal)
    )
}
