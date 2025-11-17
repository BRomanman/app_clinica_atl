package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la tabla `Seguro`.
 */
data class SeguroDto(
    @SerializedName("id_seguro") val id: Long?,
    @SerializedName("nombre_seguro") val nombreSeguro: String,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("id_usuario") val idUsuario: Long,
    @SerializedName("fecha_creacion") val fechaCreacion: String? // DATETIME
)
