package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la tabla `Especialidad`.
 */
data class EspecialidadDto(
    @SerializedName("id_especialidad") val id: Long?,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("id_doctor") val idDoctor: Long
)
