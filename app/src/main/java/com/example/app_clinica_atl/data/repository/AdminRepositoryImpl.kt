package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.AdministradorDto
import com.example.app_clinica_atl.data.remote.dto.AdministradorUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.DoctorCreateRequestDto
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.remote.dto.ChangePasswordRequestDto
import com.example.app_clinica_atl.data.remote.dto.DoctorUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadUpdateRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class AdminRepositoryImpl(
    private val api: UsuariosApi = RetrofitClient.usuariosApi
) : AdminRepository {

    // =========================
    // PERFIL ADMINISTRADOR
    // =========================

    override suspend fun getAdminProfile(adminId: Long): Result<AdministradorDto> =
        withContext(Dispatchers.IO) {
            val response = api.getAdminById(adminId)
            response.toResult("administrador")
        }

    override suspend fun updateAdminProfile(
        adminId: Long,
        request: AdministradorUpdateRequestDto
    ): Result<AdministradorDto> =
        withContext(Dispatchers.IO) {
            val response = api.updateAdmin(adminId, request)
            response.toResult("administrador")
        }

    override suspend fun changeAdminPassword(
        adminId: Long,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            val request = ChangePasswordRequestDto(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            val response = api.changeAdminPassword(adminId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val code = response.code()
                val message = if (code in listOf(400, 401, 403)) {
                    "WRONG_CURRENT_PASSWORD"
                } else {
                    response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                        ?: "No se pudo actualizar la contraseña."
                }
                Result.failure(Exception(message))
            }
        }

    override suspend fun uploadAdminProfilePhoto(
        adminId: Long,
        imageFile: File
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val part = createImagePart(imageFile)
                val response = api.uploadAdminProfilePhoto(adminId, part)
                if (!response.isSuccessful) {
                    throw HttpException(response)
                }
            }
        }

    override fun buildAdminProfilePhotoUrl(adminId: Long): String {
        return "${RetrofitClient.BASE_URL_USUARIO}administradores/$adminId/foto-perfil"
    }

    // =========================
    // DOCTORES
    // =========================

    override suspend fun getAllDoctors(): Result<List<DoctorDto>> =
        withContext(Dispatchers.IO) {
            val response = api.getDoc()
            response.toResult("doctores")
        }

    override suspend fun getDoctorById(doctorId: Long): Result<DoctorDto> =
        withContext(Dispatchers.IO) {
            val response = api.getDocById(doctorId)
            response.toResult("doctor")
        }

    override suspend fun createDoctor(
        request: DoctorCreateRequestDto
    ): Result<DoctorDto> =
        withContext(Dispatchers.IO) {
            val response = api.createDoctor(request)
            response.toResult("doctor")
        }

    override suspend fun updateDoctor(
        doctorId: Long,
        request: DoctorUpdateRequestDto
    ): Result<DoctorDto> =
        withContext(Dispatchers.IO) {
            val response = api.updateDoc(doctorId, request)
            response.toResult("doctor")
        }

    override suspend fun deactivateDoctor(doctorId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.deleteDoc(doctorId)
                if (!response.isSuccessful) {
                    throw HttpException(response)
                }
            }
        }

    // =========================
    // ESPECIALIDADES
    // =========================

    override suspend fun getAllSpecialties(): Result<List<EspecialidadDto>> =
        withContext(Dispatchers.IO) {
            val response = api.getAllSpecialties()
            response
                .toResult("especialidades")
                .map { list -> list.mapNotNull { it.toDtoOrNull() } }
        }

    override suspend fun createSpecialty(
        request: EspecialidadRequestDto
    ): Result<EspecialidadDto> =
        withContext(Dispatchers.IO) {
            val response = api.createSpecialty(request)
            response
                .toResult("especialidad")
                .mapCatching {
                    it.toDtoOrNull() ?: throw IllegalStateException("Especialidad sin nombre")
                }
        }

    override suspend fun updateSpecialty(
        id: Long,
        request: EspecialidadUpdateRequestDto
    ): Result<EspecialidadDto> =
        withContext(Dispatchers.IO) {
            val response = api.updateSpecialty(id, request)
            response
                .toResult("especialidad")
                .mapCatching {
                    it.toDtoOrNull() ?: throw IllegalStateException("Especialidad sin nombre")
                }
        }

    override suspend fun deleteSpecialty(id: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.deleteSpecialty(id)
                if (!response.isSuccessful) {
                    throw HttpException(response)
                }
            }
        }

    // =========================
    // HELPERS PRIVADOS
    // =========================

    private fun createImagePart(imageFile: File): MultipartBody.Part {
        val requestBody = imageFile
            .asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            "file",
            imageFile.name,
            requestBody
        )
    }
}

// =========================
// HELPERS TOP-LEVEL
// =========================

private fun <T> Response<T>.toResult(label: String): Result<T> {
    return if (isSuccessful) {
        val body = body()
        if (body != null) {
            Result.success(body)
        } else {
            Result.failure(IllegalStateException("$label: respuesta vacía"))
        }
    } else {
        Result.failure(HttpException(this))
    }
}

private fun EspecialidadResponseDto.toDtoOrNull(): EspecialidadDto? {
    val cleanName = nombre?.trim().orEmpty()
    if (cleanName.isBlank()) return null
    return EspecialidadDto(
        id = id ?: 0L,
        name = cleanName,
        price = 0.0
    )
}
