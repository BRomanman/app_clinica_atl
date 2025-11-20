package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la tabla `Historial`.
 */
data class HistorialDto(
    @SerializedName(value = "id_historial", alternate = ["id"]) val id: Long?,
    @SerializedName(value = "id_usuario", alternate = ["usuarioId"]) val idUsuario: Long,
    @SerializedName(value = "fecha_consulta", alternate = ["fechaConsulta", "fecha"]) val fechaConsulta: String, // DATETIME
    @SerializedName(value = "diagnostico", alternate = ["diagnosis"]) val diagnostico: String,
    @SerializedName(value = "observaciones", alternate = ["observacion", "notes"]) val observaciones: String
)
