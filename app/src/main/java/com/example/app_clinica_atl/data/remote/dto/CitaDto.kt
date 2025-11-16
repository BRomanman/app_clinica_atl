package com.example.app_clinica_atl.data.remote.dto

data class CitaDto(
    val id: Long,
    val patientId: Long,
    val doctorId: Long,
    val date: String, // "YYYY-MM-DD"
    val time: String, // "HH:mm"
    val specialty: String,
    val status: String // e.g., "confirmed", "cancelled"
)
