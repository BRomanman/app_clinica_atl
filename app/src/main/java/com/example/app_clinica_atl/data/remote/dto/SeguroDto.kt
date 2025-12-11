package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName


data class SeguroDto(
    @SerializedName(value = "id", alternate = ["id_seguro"]) val id: Long = 0,
    @SerializedName(value = "nombre", alternate = ["nombreSeguro", "nombre_seguro"]) val name: String = "",
    @SerializedName("descripcion") val description: String = "",
    @SerializedName(value = "precio", alternate = ["valor"]) val price: Int = 0
)
