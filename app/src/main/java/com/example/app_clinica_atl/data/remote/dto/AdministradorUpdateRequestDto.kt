package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdministradorUpdateRequestDto(
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("apellido")
    val apellido: String,
    @SerializedName("correo")
    val correo: String,
    @SerializedName("telefono")
    val telefono: String
)
