package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para /api/v1/doctores que refleja los campos de la tabla Doctores en el backend.
 */
data class DoctorCreateRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("contrasena") val contrasena: String,
    @SerializedName("idRol") val idRol: Long = 2L, // fk rol (2 = doctor)
    @SerializedName("idEspecialidad") val idEspecialidad: Long,
    @SerializedName("tarifaConsulta") val tarifaConsulta: Int,
    @SerializedName("sueldo") val sueldo: Long,
    @SerializedName("bono") val bono: Long = 0L, // bonus column del esquema Doctores
    @SerializedName("activo") val activo: Boolean = true // default activo en backend
) {
    fun toDoctorDto(): DoctorDto = DoctorDto(
        nombre = nombre,
        apellido = apellido,
        fechaNacimiento = fechaNacimiento,
        correo = correo,
        telefono = telefono,
        contrasena = contrasena,
        idRol = idRol,
        tipo = "Doctor",
        tarifaConsulta = tarifaConsulta,
        sueldo = sueldo,
        bono = bono,
        activo = activo,
        idEspecialidad = idEspecialidad
    )
}
