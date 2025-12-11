package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO alineado con la API normalizada de Citas en localhost:8080.
 * Ofrece alias para mantener compatibilidad con los ViewModels actuales.
 */
data class CitaDto(
    @SerializedName(value = "id", alternate = ["id_cita"])
    val id: Long,
    @SerializedName(value = "fechaCita", alternate = ["fecha_cita"])
    val fechaCita: String,
    @SerializedName(value = "horaInicio", alternate = ["hora_inicio"])
    val horaInicio: String?,
    @SerializedName(value = "horaFin", alternate = ["hora_fin"])
    val horaFin: String?,
    @SerializedName(value = "estado", alternate = ["status"])
    val estado: String,
    @SerializedName(value = "idUsuario", alternate = ["id_usuario"])
    val idUsuario: Long?,
    @SerializedName(value = "idDoctor", alternate = ["id_doctor"])
    val idDoctor: Long,
    @SerializedName(value = "disponible", alternate = ["es_disponible"])
    val disponible: Boolean = true
) {
    val patientId: Long? get() = idUsuario
    val doctorId: Long get() = idDoctor
    val date: String get() = fechaCita
    val startTime: String get() = horaInicio?.take(5).orEmpty()
    val endTime: String get() = horaFin?.take(5).orEmpty()
    val time: String get() = startTime
    val status: String get() = estado
    val available: Boolean get() = disponible
}
