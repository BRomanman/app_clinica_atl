package com.example.app_clinica_atl.data.remote.dto

/**
 * DTO simple para representar estadísticos mensuales por doctor.
 */
data class DoctorMonthlyStatDto(
    val month: String,
    val totalAppointments: Int
)
