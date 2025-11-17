package com.example.app_clinica_atl.data.remote.dto

import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity

/**
 * Mapea el DTO remoto al modelo interno que usa la app.
 */
fun UserDto.toUsuarioEntity(): UsuarioEntity {
    val roleString = when (idRol) {
        1L -> "admin"
        2L -> "doctor"
        3L -> "paciente"
        else -> "paciente"
    }

    return UsuarioEntity(
        id = id ?: 0L,
        name = listOfNotNull(nombre, apellido).joinToString(" ").trim(),
        email = correo,
        phone = telefono ?: "",
        password = contrasena,
        profileImageUrl = null,
        role = roleString,
        specialty = null,
        salary = null
    )
}
