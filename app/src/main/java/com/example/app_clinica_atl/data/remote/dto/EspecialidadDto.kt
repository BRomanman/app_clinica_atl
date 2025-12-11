package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EspecialidadDto(
    @SerializedName(value = "id", alternate = ["id_especialidad", "idEspecialidad"])
    val id: Long = 0,
    @SerializedName(value = "nombre", alternate = ["name", "especialidad"])
    val name: String,
    @SerializedName(value = "precio", alternate = ["price", "valor"])
    val price: Double = 0.0
)
