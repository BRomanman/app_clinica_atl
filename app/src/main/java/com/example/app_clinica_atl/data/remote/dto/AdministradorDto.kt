package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdministradorDto(
    @SerializedName(value = "id", alternate = ["id_admin", "idAdministrador"])
    val id: Long? = null,
    @SerializedName("nombre")
    val nombre: String? = null,
    @SerializedName("apellido")
    val apellido: String? = null,
    @SerializedName("correo")
    val correo: String? = null,
    @SerializedName("telefono")
    val telefono: String? = null
)
