package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RolIdDto(
    @SerializedName(value = "id", alternate = ["idRol", "id_rol"])
    val id: Long
)
