package com.example.app_clinica_atl.data.remote.dto

/**
 * Funciones de ayuda para convertir las respuestas crudas de la API
 * (`UsuarioResponseDto`, `LoginResponseDto`, etc.) al modelo que utiliza
 * la aplicación (`UsuarioDto`).
 */

fun UsuarioResponseDto.toUsuarioDto(): UsuarioDto {
    val displayName = listOfNotNull(nombre, apellido).joinToString(" ").trim()
    val normalizedRole = rol?.lowercase() ?: "paciente"
    return UsuarioDto(
        id = id,
        name = if (displayName.isBlank()) correo.orEmpty() else displayName,
        email = correo.orEmpty(),
        phone = telefono.orEmpty(),
        password = "",
        profileImageUrl = null,
        role = normalizedRole,
        specialty = null,
        salary = doctor?.sueldo?.toDouble()
    )
}

fun List<UsuarioResponseDto>.toUsuarioDtoList(): List<UsuarioDto> = map { it.toUsuarioDto() }
