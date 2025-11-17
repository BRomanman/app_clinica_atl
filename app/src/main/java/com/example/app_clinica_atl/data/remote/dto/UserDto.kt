package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la tabla `Usuario`.
 * Se usan anotaciones para mapear los nombres snake_case de la API.
 */
data class UserDto(
    @SerializedName("id_usuario") val id: Long?,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String, // DATETIME -> ISO8601 recomendado
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("contrasena") val contrasena: String,
    @SerializedName("id_rol") val idRol: Long
)
