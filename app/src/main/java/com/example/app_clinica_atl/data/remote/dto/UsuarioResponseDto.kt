package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioResponseDto(
    @SerializedName(value = "id", alternate = ["idUsuario", "id_usuario"])
    val id: Long,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("apellido") val apellido: String?,
    @SerializedName(value = "fechaNacimiento", alternate = ["fecha_nacimiento"])
    val fechaNacimiento: String?,
    @SerializedName("correo") val correo: String?,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName(value = "idRol", alternate = ["id_rol"])
    val idRol: Long? = null,
    @SerializedName(value = "rol", alternate = ["rolNombre", "tipo"])
    val rol: Any? = null,
    @SerializedName(value = "rolInfo", alternate = ["rolDto", "rolObj"])
    val rolInfo: RolRequest? = null,
    @SerializedName("doctor") val doctor: DoctorInfoDto?,
    @SerializedName(value = "trabajador", alternate = ["empleado"])
    val trabajador: DoctorInfoDto? = null,
    @SerializedName(value = "imagenPerfil", alternate = ["imagen_perfil"])
    val imagenPerfil: String? = null
)

data class DoctorInfoDto(
    @SerializedName(value = "id", alternate = ["idDoctor", "id_doctor", "id_trabajador"])
    val id: Long?,
    @SerializedName(value = "tarifaConsulta", alternate = ["tarifa_consulta"])
    val tarifaConsulta: Int?,
    @SerializedName(value = "sueldo", alternate = ["salario", "pay"])
    val sueldo: Long?,
    @SerializedName("bono") val bono: Long?,
    @SerializedName("activo") val activo: Boolean? = null,
    @SerializedName(value = "idEspecialidad", alternate = ["id_especialidad"])
    val idEspecialidad: Long? = null,
    @SerializedName(value = "tipo", alternate = ["tipo_trabajador"])
    val tipo: String? = null
)

data class UsuarioUpdateRequestDto(
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("apellido") val apellido: String? = null,
    @SerializedName(value = "fechaNacimiento", alternate = ["fecha_nacimiento"])
    val fechaNacimiento: String? = null,
    @SerializedName("correo") val correo: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("contrasena") val contrasena: String? = null,
    @SerializedName(value = "idRol", alternate = ["id_rol"])
    val idRol: Long? = null,
    @SerializedName("rol") val rol: RolRequest? = null,
    @SerializedName(value = "imagenPerfil", alternate = ["imagen_perfil"])
    val imagenPerfil: String? = null
)

data class RolRequest(
    @SerializedName(value = "id", alternate = ["idRol", "id_rol"])
    val id: Long,
    @SerializedName(value = "nombre", alternate = ["rol", "role"])
    val nombre: String? = null
)
