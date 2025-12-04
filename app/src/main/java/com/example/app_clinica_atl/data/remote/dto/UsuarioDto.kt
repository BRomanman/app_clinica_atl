package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO genérico que reemplaza al antiguo `UsuarioEntity`.
 * Mantiene los nombres de campos usados por la app pero
 * permite mapearlos directamente desde/para la API REST.
 */
data class UsuarioDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("nombre") val name: String,
    @SerializedName("correo") val email: String,
    @SerializedName("telefono") val phone: String,
    @SerializedName("contrasena") val password: String,
    @SerializedName("imagenPerfil") val profileImageUrl: String? = null,
    @SerializedName("rol") val role: String,
    @SerializedName("especialidad") val specialty: String? = null,
    @SerializedName("salario") val salary: Double? = null,
    @SerializedName("fechaNacimiento") val birthDate: String? = null,
    @SerializedName(value = "doctorId", alternate = ["idDoctor"]) val doctorId: Long? = null
)
