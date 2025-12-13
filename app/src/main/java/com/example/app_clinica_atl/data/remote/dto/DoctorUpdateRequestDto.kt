package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la actualización completa del doctor según la tabla Doctores.
 */
data class DoctorUpdateRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("idEspecialidad") val idEspecialidad: Long,
    @SerializedName("tarifaConsulta") val tarifaConsulta: Int,
    @SerializedName("sueldo") val sueldo: Long,
    @SerializedName("bono") val bono: Long = 0L,
    @SerializedName("activo") val activo: Boolean
)
