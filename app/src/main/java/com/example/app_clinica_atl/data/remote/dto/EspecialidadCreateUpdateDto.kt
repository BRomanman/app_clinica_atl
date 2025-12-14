package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para crear una especialidad asociada a un doctor.
 *
 * Backend (POST /api/v1/especialidades) espera:
 * {
 *   "nombre": "Cardiología",
 *   "doctorId": 7
 * }
 */
data class EspecialidadCreateRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("doctorId") val doctorId: Long
)

/**
 * DTO para actualizar una especialidad existente.
 *
 * Backend (PUT /api/v1/especialidades/{id}) permite:
 * - cambiar sólo el nombre
 * - cambiar sólo el doctor
 * - cambiar ambos
 *
 * Por eso los campos son opcionales.
 */
data class EspecialidadUpdateRequestDto(
    @SerializedName("nombre") val nombre: String? = null
)
