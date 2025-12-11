package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.example.app_clinica_atl.data.remote.dto.normalizeRole

data class LoginRequestDto(
    @SerializedName(value = "correo", alternate = ["email"]) val correo: String,
    @SerializedName("contrasena") val contrasena: String
)

data class LoginResponseDto(
    @SerializedName(value = "userId", alternate = ["id", "idUsuario", "id_usuario"])
    val userId: Long,
    @SerializedName(value = "role", alternate = ["rol", "rolNombre"])
    val role: String,
    @SerializedName(value = "idRol", alternate = ["id_rol"])
    val roleId: Long? = null,
    @SerializedName(value = "doctorId", alternate = ["idDoctor", "id_doctor", "id_trabajador"])
    val doctorId: Long?,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName(value = "correo", alternate = ["email"]) val correo: String,
    @SerializedName("token") val token: String?
)




fun LoginResponseDto.toUsuarioDtoFromLogin(): UsuarioDto {
    val normalizedRole = normalizeRole(role, roleId)

    val displayName = listOfNotNull(nombre.takeIf { it.isNotBlank() }, apellido.takeIf { it.isNotBlank() })
        .joinToString(" ")
        .ifBlank { correo }

    return UsuarioDto(
        id = userId,
        name = displayName,
        lastName = apellido,
        email = correo,
        phone = "",
        password = "",
        profileImageUrl = null,
        role = normalizedRole,
        roleId = roleId,
        specialty = null,
        salary = null,
        birthDate = null,
        doctorId = doctorId
    )
}
