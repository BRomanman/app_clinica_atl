package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO genérico que reemplaza al antiguo `UsuarioEntity`.
 * Mantiene los nombres de campos usados por la app pero
 * permite mapearlos directamente desde/para la API REST.
 */
data class UsuarioDto(
    @SerializedName(value = "id", alternate = ["id_usuario", "idUsuario"])
    val id: Long = 0,

    @SerializedName("nombre") val name: String,
    @SerializedName(value = "apellido", alternate = ["apellidos"])
    val lastName: String? = null,
    @SerializedName(value = "fechaNacimiento", alternate = ["fecha_nacimiento"])
    val birthDate: String? = null,
    @SerializedName("correo") val email: String,
    @SerializedName("telefono") val phone: String,
    @SerializedName("contrasena") val password: String,
    @SerializedName(value = "imagenPerfil", alternate = ["imagen_perfil"])
    val profileImageUrl: String? = null,
    @SerializedName(value = "rol", alternate = ["tipo", "rol_nombre"])
    val role: String,
    @SerializedName(value = "idRol", alternate = ["id_rol"])
    val roleId: Long? = null,
    @SerializedName("especialidad") val specialty: String? = null,
    @SerializedName(value = "salario", alternate = ["sueldo"])
    val salary: Double? = null,
    @SerializedName(value = "doctorId", alternate = ["idDoctor", "id_doctor", "id_trabajador"])
    val doctorId: Long? = null
)
