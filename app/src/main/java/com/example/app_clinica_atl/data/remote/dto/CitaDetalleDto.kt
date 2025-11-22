package com.example.app_clinica_atl.data.remote.dto

/**
 * DTO auxiliar para mostrar el resultado de joins entre citas y doctores.
 */
data class CitaDetalleDto(
    val appointmentId: Long?,
    val doctorName: String,
    val doctorSpecialty: String,
    val date: String,
    val time: String,
    val status: String
)
