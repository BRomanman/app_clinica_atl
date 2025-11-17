package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la tabla `Cita`.
 */
data class CitaDto(
    @SerializedName("id_cita") val id: Long?,
    @SerializedName("fecha_cita") val fechaCita: String, // DATETIME
    @SerializedName("estado") val estado: String,
    @SerializedName("id_usuario") val idUsuario: Long,
    @SerializedName("id_doctor") val idDoctor: Long,
    @SerializedName("id_pago") val idPago: Long?,
    @SerializedName("id_receta") val idReceta: Long?,
    @SerializedName("id_resena") val idResena: Long?,
    @SerializedName("id_resumen") val idResumen: Long?,
    @SerializedName("id_consulta") val idConsulta: Long?
)
