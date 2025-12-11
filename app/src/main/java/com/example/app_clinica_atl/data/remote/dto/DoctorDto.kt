package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la tabla `Doctor` (datos adicionales al usuario base).
 */
data class DoctorDto(
    @SerializedName(value = "id", alternate = ["id_doctor", "idDoctor", "id_trabajador"])
    val id: Long? = null,

    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("apellido") val apellido: String? = null,
    @SerializedName(value = "fechaNacimiento", alternate = ["fecha_nacimiento"])
    val fechaNacimiento: String? = null,
    @SerializedName("correo") val correo: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("contrasena") val contrasena: String? = null,
    @SerializedName(value = "idRol", alternate = ["id_rol"])
    val idRol: Long? = null,
    @SerializedName(value = "tipo", alternate = ["tipo_trabajador", "rol"])
    val tipo: String? = null,

    @SerializedName(value = "tarifaConsulta", alternate = ["tarifa_consulta"])
    val tarifaConsulta: Int? = null,
    @SerializedName(value = "sueldo", alternate = ["pay", "salario"])
    val sueldo: Long? = null,
    @SerializedName("bono") val bono: Long? = null,
    @SerializedName("activo") val activo: Boolean? = null,
    @SerializedName(value = "idEspecialidad", alternate = ["id_especialidad"])
    val idEspecialidad: Long? = null,

    @SerializedName("usuario") val usuario: UsuarioResponseDto? = null,
    @SerializedName(
        value = "especialidad",
        alternate = ["nombreEspecialidad", "especialidad_nombre"]
    )
    val especialidad: String? = null
)
