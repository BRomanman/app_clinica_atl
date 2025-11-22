package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la tabla `Doctor` (datos adicionales al usuario base).
 */
data class DoctorDto(
    @SerializedName(value = "id", alternate = ["id_doctor"]) val id: Long?,
    @SerializedName(value = "tarifaConsulta", alternate = ["tarifa_consulta"]) val tarifaConsulta: Int,
    @SerializedName(value = "sueldo", alternate = ["pay"]) val sueldo: Long?,
    @SerializedName("bono") val bono: Long?,
    @SerializedName("activo") val activo: Int, // 0 o 1
    @SerializedName("usuario") val usuario: UsuarioResponseDto? = null,
    @SerializedName("especialidad") val especialidad: String? = null
)
