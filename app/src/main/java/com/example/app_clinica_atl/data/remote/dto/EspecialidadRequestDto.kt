package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EspecialidadRequestDto(
    @SerializedName(value = "nombre", alternate = ["especialidad"])
    val nombre: String,
    @SerializedName(value = "doctorId", alternate = ["idDoctor", "id_doctor", "id_trabajador"])
    val doctorId: Long? = null,
    @SerializedName(value = "idEspecialidad", alternate = ["id_especialidad"])
    val idEspecialidad: Long? = null
)
