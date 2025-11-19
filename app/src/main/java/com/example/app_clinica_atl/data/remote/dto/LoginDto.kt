package com.example.app_clinica_atl.data.remote.dto

import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity
import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasena") val contrasena: String
)

data class LoginResponseDto(
    @SerializedName("userId") val userId: Long?,
    @SerializedName("role") val role: String?,
    @SerializedName("doctorId") val doctorId: Long?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("apellido") val apellido: String?,
    @SerializedName("correo") val correo: String?
)

/**
 * Convierte la respuesta del login remoto en la entidad que usa la app.
 * Guardamos la contraseña ingresada para mantener coherencia con el modelo local.
 */
fun LoginResponseDto.toUsuarioEntityFromLogin(plainPassword: String): UsuarioEntity {
    val normalizedRole = when (role?.lowercase()) {
        "administrador" -> "admin"
        "doctor" -> "doctor"
        "paciente" -> "paciente"
        else -> role ?: "paciente"
    }

    val fullName = listOfNotNull(nombre, apellido)
        .joinToString(" ")
        .ifBlank { correo.orEmpty() }

    return UsuarioEntity(
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
