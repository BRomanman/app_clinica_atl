package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EspecialidadResponseDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("doctorId") val doctorId: Long?
)
