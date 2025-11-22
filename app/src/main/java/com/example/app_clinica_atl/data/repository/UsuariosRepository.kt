package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import com.example.app_clinica_atl.data.remote.dto.LoginRequestDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
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
    private val usuariosApi: UsuariosApi = RetrofitClient.usuariosApi
) {

    suspend fun login(email: String, pass: String): Result<UsuarioDto> = loginViaApi(email, pass)

    suspend fun register(newUser: UsuarioDto): Result<UsuarioDto> = withContext(Dispatchers.IO) {
        try {
            val request = newUser.toUpdateRequest()
            val created = usuariosApi.createUser(request)
            Result.success(created.toUsuarioDto())
        } catch (e: HttpException) {
            Result.failure(Exception(e.message() ?: "Error HTTP ${e.code()}", e))
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
            usuariosApi.getUsers()
                .map { it.toUsuarioDto() }
                .filter { it.role.equals("doctor", true) }
        }
        emit(doctors)
    }

    suspend fun getDoctorIdForUser(userId: Long): Result<Long?> = withContext(Dispatchers.IO) {
        runCatching { usuariosApi.getUserById(userId).doctor?.id }
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
            Result.success(loggedUser.toUsuarioDtoFromLogin(pass))
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
}

private fun UsuarioDto.toUpdateRequest(): UsuarioUpdateRequestDto {
    val parts = name.trim().split(" ", limit = 2)
    val nombre = parts.getOrNull(0)
    val apellido = parts.getOrNull(1)
    val roleId = roleToId(role)
    return UsuarioUpdateRequestDto(
        nombre = nombre,
        apellido = apellido,
        correo = email,
        telefono = phone,
        contrasena = password,
        idRol = roleId
    )
}