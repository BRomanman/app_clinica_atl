package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.example.app_clinica_atl.data.remote.dto.normalizeRole

data class LoginRequestDto(
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasena") val contrasena: String
)

data class LoginResponseDto(
    @SerializedName("userId") val userId: Long,
    @SerializedName("role") val role: String,
    @SerializedName("doctorId") val doctorId: Long?,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("token") val token: String?
)




fun LoginResponseDto.toUsuarioDtoFromLogin(): UsuarioDto {
    val normalizedRole = normalizeRole(role)

    val displayName = listOfNotNull(nombre.takeIf { it.isNotBlank() }, apellido.takeIf { it.isNotBlank() })
        .joinToString(" ")
        .ifBlank { correo }

    return UsuarioDto(
        id = userId,
        name = displayName,
        email = correo,
        phone = "",
        password = "",
        profileImageUrl = null,
        role = normalizedRole,
        specialty = null,
        salary = null,
        birthDate = null,
        doctorId = doctorId
    )
}
