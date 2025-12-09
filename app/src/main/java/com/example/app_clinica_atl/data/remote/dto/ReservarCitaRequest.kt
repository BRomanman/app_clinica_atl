package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReservarCitaRequest(
    @SerializedName("idUsuario") val idUsuario: Long
)
