package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO que reemplaza a la entidad local `SeguroEntity`.
 */
data class SeguroDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("nombre") val name: String,
    @SerializedName("descripcion") val description: String,
    @SerializedName("precio") val price: Double
)
