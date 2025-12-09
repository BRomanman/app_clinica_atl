package com.example.app_clinica_atl.data.remote

import com.google.gson.annotations.SerializedName

/**
 * DTO alineado con la API normalizada de Citas en localhost:8080.
 * Ofrece alias para mantener compatibilidad con los ViewModels actuales.
 */
data class CitaDto(
    @SerializedName("id") val id: Long,
    @SerializedName("fechaCita") val fechaCita: String,
    @SerializedName("horaInicio") val horaInicio: String?,
    @SerializedName("horaFin") val horaFin: String?,
    @SerializedName("estado") val estado: String,
    @SerializedName("idUsuario") val idUsuario: Long?,
    @SerializedName("idDoctor") val idDoctor: Long,
    @SerializedName("disponible") val disponible: Boolean
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
