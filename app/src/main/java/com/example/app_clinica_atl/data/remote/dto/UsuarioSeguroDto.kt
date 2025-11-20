package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO que reemplaza a la tabla de unión `UsuarioSeguroEntity`.
 */
data class UsuarioSeguroDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("pacienteId") val patientId: Long,
    @SerializedName("seguroId") val insuranceId: Long,
    @SerializedName("estado") val status: String
)
