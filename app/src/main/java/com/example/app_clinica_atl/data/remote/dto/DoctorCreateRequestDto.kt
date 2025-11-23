package com.example.app_clinica_atl.data.remote.dto


data class DoctorCreateRequestDto(
    val tarifaConsulta: Int,   // <-- camelCase
    val sueldo: Long?,
    val bono: Long? = 0,
    val usuario: UsuarioIdRefDto
)

data class UsuarioIdRefDto(val id: Long)
