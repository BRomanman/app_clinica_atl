package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName


data class SeguroDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombreSeguro") val name: String,
    @SerializedName("descripcion") val description: String,
    @SerializedName("valor") val price: Int
)