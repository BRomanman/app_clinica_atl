package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasena") val contrasena: String
)

data class LoginResponseDto(
    @SerializedName(value = "userId", alternate = ["id", "idUsuario"]) val userId: Long?,
    @SerializedName(value = "role", alternate = ["rol", "idRol"]) val role: String?,
    @SerializedName(value = "doctorId", alternate = ["idDoctor"]) val doctorId: Long?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("apellido") val apellido: String?,
    @SerializedName("correo") val correo: String?
)



//todo arreglar flujo admin
fun LoginResponseDto.toUsuarioDtoFromLogin(plainPassword: String): UsuarioDto {
    val normalizedRole = normalizeRole(role)

    val fullName = listOfNotNull(nombre, apellido)
        .joinToString(" ")
        .ifBlank { correo.orEmpty() }

    return UsuarioDto(
        id = userId ?: 0L,
        name = fullName,
        email = correo.orEmpty(),
        phone = "",
        password = plainPassword,
        profileImageUrl = null,
        role = normalizedRole,
        specialty = null,
        salary = null
    )
}
