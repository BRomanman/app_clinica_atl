package com.example.app_clinica_atl.data.remote.dto

/**
 * Funciones de ayuda para convertir las respuestas crudas de la API
 * (`UsuarioResponseDto`, `LoginResponseDto`, etc.) al modelo que utiliza
 * la aplicación (`UsuarioDto`).
 */

/**
 * Normaliza el rol que llega desde la API (texto o id numérico) a los
 * valores usados en la app: "paciente", "doctor" o "administrador".
 */
fun normalizeRole(rawRole: String?): String {
    val cleaned = rawRole?.trim().orEmpty()
    val numericCode = cleaned.toLongOrNull()
    return when {
        numericCode == 3L -> "administrador"
        numericCode == 2L -> "doctor"
        numericCode == 1L -> "paciente"
        cleaned.equals("administrador", true) ||
                cleaned.equals("administrativo", true) ||
                cleaned.equals("admin", true) -> "administrador"
        cleaned.equals("doctor", true) || cleaned.equals("medico", true) -> "doctor"
        cleaned.equals("paciente", true) || cleaned.equals("usuario", true) -> "paciente"
        else -> "paciente"
    }
}


fun roleToId(role: String?): Long = when (normalizeRole(role)) {
    "administrador" -> 3L
    "doctor" -> 2L
    else -> 1L
}

fun UsuarioResponseDto.toUsuarioDto(): UsuarioDto {
    val displayName = listOfNotNull(nombre, apellido).joinToString(" ").trim()
    val normalizedRole = normalizeRole(rol)
    return UsuarioDto(
        id = id,
        name = if (displayName.isBlank()) correo.orEmpty() else displayName,
        email = correo.orEmpty(),
        phone = telefono.orEmpty(),
        password = "",
        profileImageUrl = imagenPerfil,
        role = normalizedRole,
        specialty = null,
        salary = doctor?.sueldo?.toDouble()
    )
}

fun List<UsuarioResponseDto>.toUsuarioDtoList(): List<UsuarioDto> = map { it.toUsuarioDto() }
