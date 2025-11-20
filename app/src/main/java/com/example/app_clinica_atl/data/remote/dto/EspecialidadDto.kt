package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO que reemplaza a la antigua `EspecialidadEntity`.
 */
data class EspecialidadDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("nombre") val name: String,
    @SerializedName("precio") val price: Double
)
