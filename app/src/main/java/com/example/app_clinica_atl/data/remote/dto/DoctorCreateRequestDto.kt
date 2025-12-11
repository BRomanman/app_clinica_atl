package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName


data class DoctorCreateRequestDto(
    @SerializedName("tarifaConsulta") val tarifaConsulta: Int,
    @SerializedName("sueldo") val sueldo: Long?,
    @SerializedName("bono") val bono: Long? = 0,
    @SerializedName("usuario") val usuario: UsuarioIdRefDto? = null,
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("apellido") val apellido: String? = null,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String? = null,
    @SerializedName("correo") val correo: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("contrasena") val contrasena: String? = null,
    @SerializedName("idRol") val idRol: Long? = null,
    @SerializedName("tipo") val tipo: String? = null,
    @SerializedName("idEspecialidad") val idEspecialidad: Long? = null
) {
    fun toDoctorDto(): DoctorDto = DoctorDto(
        id = null,
        nombre = nombre,
        apellido = apellido,
        fechaNacimiento = fechaNacimiento,
        correo = correo,
        telefono = telefono,
        contrasena = contrasena,
        idRol = idRol,
        tipo = tipo,
        tarifaConsulta = tarifaConsulta,
        sueldo = sueldo,
        bono = bono,
        idEspecialidad = idEspecialidad
    )
}

data class UsuarioIdRefDto(@SerializedName("id") val id: Long?)
