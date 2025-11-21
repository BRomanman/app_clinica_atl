package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la tabla `Historial`.
 */
data class HistorialDto(
    @SerializedName(value = "id_historial", alternate = ["id"]) val id: Long? = null,
    @SerializedName(value = "id_usuario", alternate = ["usuarioId", "idUsuario"]) val idUsuario: Long? = null,
    @SerializedName(value = "id_doctor", alternate = ["doctorId", "idDoctor"]) val idDoctor: Long? = null,
    @SerializedName(
        value = "fecha_consulta",
        alternate = ["fechaConsulta", "fecha", "fechaCita"]
    ) val fechaConsulta: String? = null, // DATETIME
    @SerializedName(
        value = "diagnostico",
        alternate = ["diagnosis", "estado"]
    ) val diagnostico: String? = null,
    @SerializedName(value = "estado", alternate = ["status"]) val estado: String? = null,
    @SerializedName(
        value = "observaciones",
        alternate = ["observacion", "notes", "observacionesHorario"]
    ) val observaciones: String? = null
)
