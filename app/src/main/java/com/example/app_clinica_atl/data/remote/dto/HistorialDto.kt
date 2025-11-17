package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la tabla `Historial`.
 */
data class HistorialDto(
    @SerializedName("id_historial") val id: Long?,
    @SerializedName("id_usuario") val idUsuario: Long,
    @SerializedName("fecha_consulta") val fechaConsulta: String, // DATETIME
    @SerializedName("diagnostico") val diagnostico: String,
    @SerializedName("observaciones") val observaciones: String
)
