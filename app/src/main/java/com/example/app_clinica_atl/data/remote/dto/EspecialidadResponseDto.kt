package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Respuesta flexible para especialidades. El backend ha devuelto los campos
 * con distintos nombres según el servicio, por lo que aceptamos varios alias.
 */
data class EspecialidadResponseDto(
    @SerializedName(value = "id", alternate = ["idEspecialidad", "id_especialidad"])
    val id: Long? = null,

    @SerializedName(
        value = "nombre",
        alternate = ["especialidad", "nombreEspecialidad", "nombre_especialidad", "name"]
    )
    val nombre: String? = null,

    @SerializedName(value = "doctorId", alternate = ["idDoctor", "doctor_id"])
    val doctorId: Long? = null
)
