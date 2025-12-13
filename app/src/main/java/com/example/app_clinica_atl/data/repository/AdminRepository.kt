package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.dto.AdministradorDto
import com.example.app_clinica_atl.data.remote.dto.AdministradorUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.DoctorCreateRequestDto
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadUpdateRequestDto
import java.io.File

/**
 * Repositorio para todo el flujo de administrador:
 *  - perfil de admin
 *  - foto de perfil
 *  - doctores
 *  - especialidades
 */
interface AdminRepository {

    // --- PERFIL ADMINISTRADOR ---

    suspend fun getAdminProfile(adminId: Long): Result<AdministradorDto>

    suspend fun updateAdminProfile(
        adminId: Long,
        request: AdministradorUpdateRequestDto
    ): Result<AdministradorDto>

    suspend fun uploadAdminProfilePhoto(
        adminId: Long,
        imageFile: File
    ): Result<Unit>

    /**
     * URL absoluta para que Coil cargue la foto de perfil del admin.
     */
    fun buildAdminProfilePhotoUrl(adminId: Long): String

    // --- DOCTORES ---

    suspend fun getAllDoctors(): Result<List<DoctorDto>>

    suspend fun getDoctorById(doctorId: Long): Result<DoctorDto>

    suspend fun createDoctor(request: DoctorCreateRequestDto): Result<DoctorDto>

    suspend fun updateDoctor(
        doctorId: Long,
        request: DoctorDto
    ): Result<DoctorDto>

    suspend fun deactivateDoctor(doctorId: Long): Result<Unit>

    // --- ESPECIALIDADES ---

    suspend fun getAllSpecialties(): Result<List<EspecialidadDto>>

    suspend fun createSpecialty(
        request: EspecialidadRequestDto
    ): Result<EspecialidadDto>

    suspend fun updateSpecialty(
        id: Long,
        request: EspecialidadUpdateRequestDto
    ): Result<EspecialidadDto>

    suspend fun deleteSpecialty(id: Long): Result<Unit>
}
