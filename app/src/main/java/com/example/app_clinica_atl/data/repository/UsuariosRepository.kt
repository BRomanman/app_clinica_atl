package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.DoctorCreateRequestDto // <-- IMPORT AÑADIDO
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.LoginRequestDto
import com.example.app_clinica_atl.data.remote.dto.RolRequest
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioResponseDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.roleToId
import com.example.app_clinica_atl.data.remote.dto.toUsuarioDto
import com.example.app_clinica_atl.data.remote.dto.toUsuarioDtoFromLogin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

/*
* Los repositorios son los encargados de acceder a los datos de la API y describir sus funciones*
*/


class UsuariosRepository(
    private val userPreferences: UserPreferences,
    private val usuariosApi: UsuariosApi = RetrofitClient.usuariosApi
) {

    // login básico sin validación de credenciales. Luego este lo utilizamos
    // para loginViaApi
    suspend fun login(email: String, pass: String): Result<UsuarioDto> = loginViaApi(email, pass)


    /*
    * Si responde bien, guarda en UserPreferences todos los datos de sesión
    * : userId, role, doctorId, nombre, apellido, correo, y el token JWT (token).
    * El token queda cacheado en memoria gracias a UserPreferences
    *
    */
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
            //guardamos en usuario DTO
            Result.success(loggedUser.toUsuarioDtoFromLogin())
        } catch (e: HttpException) {
            val message = if (e.code() == 401) "Credenciales inválidas." else e.message()
            Result.failure(Exception(message ?: "Error HTTP ${e.code()}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }





    // por defecto en rol = 1 (paciente)
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
    // esta funcion ayuda que se eviten los errores en caso de no existir el usuario
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
        val (doctors, specialties) = withContext(Dispatchers.IO) {
            val doctorList = runCatching {
                val response = usuariosApi.getDoc()
                if (response.isSuccessful) response.body().orEmpty() else emptyList()
            }.getOrElse { emptyList() }

            val specialtyList = runCatching {
                val response = usuariosApi.getAllSpecialties()
                if (response.isSuccessful) response.body().orEmpty() else emptyList()
            }.getOrElse { emptyList() }

            doctorList to specialtyList
        }

        val specialtyById: Map<Long, String> = specialties.mapNotNull { spec ->
            val id = spec.id ?: return@mapNotNull null
            val name = spec.nombre?.trim()?.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            id to name
        }.toMap()

        val specialtyByDoctorId: Map<Long, String> = specialties.mapNotNull { spec ->
            val doctorId = spec.doctorId ?: return@mapNotNull null
            val name = spec.nombre?.trim()?.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            doctorId to name
        }.toMap()

        val doctorsWithSpecialty = doctors.map { doctorDto ->
            val baseUser = doctorDto.toUsuarioDto()
            val resolvedSpecialty = resolveDoctorSpecialty(
                doctorDto = doctorDto,
                currentSpecialty = baseUser.specialty,
                specialtyByDoctorId = specialtyByDoctorId,
                specialtyById = specialtyById
            )
            baseUser.copy(specialty = resolvedSpecialty)
        }

        emit(doctorsWithSpecialty)
    }

    // Usa el doctorId cacheado (DataStore) para no depender del backend en cada consulta
    // y evita fallos en agenda cuando /usuarios/{id} responde 404/204.
    suspend fun getDoctorIdForUser(userId: Long): Result<Long?> = withContext(Dispatchers.IO) {
        runCatching {
            val cached = userPreferences.userDoctorIdFlow.firstOrNull()
            if (cached != null) return@runCatching cached

            val user = usuariosApi.getUserById(userId)
            user.doctor?.id ?: user.trabajador?.id ?: userId
        }
    }

    suspend fun getAllSpecialties(): Result<List<EspecialidadResponseDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = usuariosApi.getAllSpecialties()
            if (response.isSuccessful) response.body().orEmpty() else throw HttpException(response)
        }
    }

    suspend fun uploadPatientProfilePhoto(imageFile: File): Result<Unit> {
        val userId = userPreferences.userIdFlow.firstOrNull()
        return uploadProfilePhotoForId(
            entityId = userId,
            imageFile = imageFile,
            missingEntityMessage = "No se pudo encontrar al paciente autenticado.",
            uploadCall = usuariosApi::uploadUserProfilePhoto
        )
    }

    suspend fun uploadDoctorProfilePhoto(imageFile: File): Result<Unit> {
        val doctorId = userPreferences.userDoctorIdFlow.firstOrNull()
        return uploadProfilePhotoForId(
            entityId = doctorId,
            imageFile = imageFile,
            missingEntityMessage = "No fue posible identificar al doctor logueado.",
            uploadCall = usuariosApi::uploadDoctorProfilePhoto
        )
    }

    suspend fun uploadAdminProfilePhoto(imageFile: File): Result<Unit> {
        val adminId = userPreferences.userIdFlow.firstOrNull()
        return uploadProfilePhotoForId(
            entityId = adminId,
            imageFile = imageFile,
            missingEntityMessage = "No se pudo encontrar al administrador autenticado.",
            uploadCall = usuariosApi::uploadAdminProfilePhoto
        )
    }

    fun buildPatientProfilePhotoUrl(userId: Long): String =
        "${RetrofitClient.BASE_URL_USUARIO}usuarios/$userId/foto-perfil"

    fun buildDoctorProfilePhotoUrl(doctorId: Long): String =
        "${RetrofitClient.BASE_URL_USUARIO}doctores/$doctorId/foto-perfil"

    fun buildAdminProfilePhotoUrl(adminId: Long): String =
        "${RetrofitClient.BASE_URL_USUARIO}administradores/$adminId/foto-perfil"

    /** Token actual para peticiones que requieren autenticación fuera de Retrofit (ej. Coil). */
    fun currentToken(): String? = userPreferences.currentToken()



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

    suspend fun verifyUserIdentity(email: String, birthDate: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val users = usuariosApi.getUsers()
            val target = users.firstOrNull { it.correo.equals(email, true) }
                ?: return@withContext Result.failure(Exception("No encontramos una cuenta con ese correo."))
            val storedBirth = target.fechaNacimiento?.takeIf { it.isNotBlank() }?.take(10)
            val storedIso = normalizeDateToIso(storedBirth)
            val inputIso = normalizeDateToIso(birthDate)
            if (storedIso == null || inputIso == null || storedIso != inputIso) {
                return@withContext Result.failure(Exception("Los datos personales no coinciden."))
            }
            Result.success(target.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
    private suspend fun uploadProfilePhotoForId(
        entityId: Long?,
        imageFile: File,
        missingEntityMessage: String,
        uploadCall: suspend (Long, MultipartBody.Part) -> Response<Unit>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (entityId == null) return@withContext Result.failure(Exception(missingEntityMessage))
        try {
            val response = uploadCall(entityId, createImagePart(imageFile))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createImagePart(imageFile: File): MultipartBody.Part {
        val requestBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", imageFile.name, requestBody)
    }

    // todo revisar si es necesario, no se usa
    suspend fun createSpecialty(name: String) =
        Result.failure<EspecialidadResponseDto>(IllegalStateException("No se puede crear especialidad sin doctor. Usa 'Crear Doctor'."))


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


    /*
     * Crea la ficha de doctor para un usuario existente.
     * Envia tarifa_consulta obligatoria (NOT NULL en BD).
     *
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
                nombre = nombre,
                apellido = apellido,
                fechaNacimiento = fechaNacimiento,
                correo = correo,
                telefono = telefono,
                contrasena = contrasena,
                idRol = 2L,
                idEspecialidad = idEspecialidad,
                tarifaConsulta = tarifaConsulta,
                sueldo = sueldo,
                bono = bono ?: 0L,
                activo = true
            )
            val response = usuariosApi.createDoc(doctorPayload.toDoctorDto())
            if (response.isSuccessful) {
                response.body()?.toUsuarioDto()
                    ?: throw IllegalStateException("Respuesta vacía al crear doctor")
            } else {
                throw HttpException(response)
            }
        }
    }
    // --- FIN DEL CÓDIGO ACTUALIZADO ---

    private fun normalizeDateToIso(date: String?): String? {
        if (date.isNullOrBlank()) return null
        val trimmed = date.trim().take(10)
        val ddmmyyyy = Regex("^(\\d{2})-(\\d{2})-(\\d{4})$")
        val yyyymmdd = Regex("^(\\d{4})-(\\d{2})-(\\d{2})$")
        return when {
            ddmmyyyy.matches(trimmed) -> {
                val (dd, mm, yyyy) = ddmmyyyy.find(trimmed)!!.destructured
                "$yyyy-$mm-$dd"
            }
            yyyymmdd.matches(trimmed) -> trimmed
            else -> null
        }
    }

    private fun resolveDoctorSpecialty(
        doctorDto: DoctorDto,
        currentSpecialty: String?,
        specialtyByDoctorId: Map<Long, String>,
        specialtyById: Map<Long, String>
    ): String? {
        return listOfNotNull(
            currentSpecialty?.trim()?.takeIf { it.isNotEmpty() },
            doctorDto.especialidad?.trim()?.takeIf { it.isNotEmpty() },
            doctorDto.id?.let { specialtyByDoctorId[it]?.takeIf { name -> name.isNotBlank() } },
            doctorDto.idEspecialidad?.let { specialtyById[it]?.takeIf { name -> name.isNotBlank() } }
        ).firstOrNull()
    }
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
