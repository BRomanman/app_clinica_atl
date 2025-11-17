package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la tabla `Doctor` (datos adicionales al usuario base).
 */
data class DoctorDto(
    @SerializedName("id_doctor") val id: Long?,
    @SerializedName("tarifa_consulta") val tarifaConsulta: Int,
    @SerializedName("id_usuario") val idUsuario: Long,
    @SerializedName("sueldo") val sueldo: Long?,
    @SerializedName("bono") val bono: Long?,
    @SerializedName("activo") val activo: Int // 0 o 1
)
