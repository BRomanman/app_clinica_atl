package com.example.app_clinica_atl.data.local.cita

/**
 * Esta es una clase de datos (DTO - Data Transfer Object).
 * NO es una tabla de base de datos.
 * La usamos para guardar el resultado de una consulta JOIN,
 * combinando los datos de la cita con los del doctor.
 */
data class CitaDetalle(
    val appointmentId: Long,
    val doctorName: String,
    val doctorSpecialty: String,
    val date: String,
    val time: String,
    val status: String
)
