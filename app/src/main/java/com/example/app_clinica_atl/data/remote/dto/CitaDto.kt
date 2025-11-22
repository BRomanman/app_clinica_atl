package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO alineado con el microservicio de Citas (CitasAPI).
 * Expone helpers derivados (`date` y `time`) para no romper la UI previa.
 */
data class CitaDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("idUsuario") val patientId: Long? = null,
    @SerializedName("idDoctor") val doctorId: Long,
    @SerializedName("fechaCita") val dateTime: String,
    @SerializedName("horaInicio") val startTime: String = "",
    @SerializedName("horaFin") val endTime: String = "",
    @SerializedName("duracionMinutos") val durationMinutes: Int = 30,
    @SerializedName("estado") val status: String = "Disponible",
    @SerializedName("disponible") val available: Boolean = true,
    @SerializedName("pago") val paymentId: Long? = null,
    @SerializedName("idReceta") val prescriptionId: Long? = null,
    @SerializedName("idResena") val reviewId: Long? = null,
    @SerializedName("idResumen") val summaryId: Long? = null,
    @SerializedName("idConsulta") val consultationId: Long? = null,
    @SerializedName("observacionesHorario") val scheduleNotes: String? = null
) {
    val date: String
        get() = dateTime.substringBefore('T', "")

    val time: String
        get() = if (startTime.isNotBlank()) startTime.substring(0, 5) else dateTime.substringAfter('T', "").take(5)
}
