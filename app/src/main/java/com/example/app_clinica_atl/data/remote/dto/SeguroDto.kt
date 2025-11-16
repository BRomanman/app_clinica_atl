package com.example.app_clinica_atl.data.remote.dto

data class SeguroDto(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val coverageDetails: String
)
