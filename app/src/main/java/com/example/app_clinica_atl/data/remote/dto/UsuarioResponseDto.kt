package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("apellido") val apellido: String?,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String?,
    @SerializedName("correo") val correo: String?,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("rol") val rol: String?,
    @SerializedName("doctor") val doctor: DoctorInfoDto?,
    @SerializedName("imagenPerfil") val imagenPerfil: String? = null
)

data class DoctorInfoDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("tarifaConsulta") val tarifaConsulta: Int?,
    @SerializedName("sueldo") val sueldo: Long?,
    @SerializedName("bono") val bono: Long?
)

data class UsuarioUpdateRequestDto(
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("apellido") val apellido: String? = null,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String? = null,
    @SerializedName("correo") val correo: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("contrasena") val contrasena: String? = null,
    @SerializedName("idRol") val idRol: Long? = null,
    @SerializedName("imagenPerfil") val imagenPerfil: String? = null
)
