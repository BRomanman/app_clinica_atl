package com.example.app_clinica_atl.data.remote.dto

data class HistorialDto(
    val id: Long,
    val patientId: Long,
    val doctorId: Long,
    val date: String, // "YYYY-MM-DD"
    val diagnosis: String,
    val treatment: String,
    val notes: String?
)
