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
fun normalizeRole(rawRole: String?, roleId: Long? = null): String {
    when (roleId) {
        3L -> return "administrador"
        2L -> return "doctor"
        1L -> return "paciente"
    }

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


fun roleToId(role: String?, roleId: Long? = null): Long = when (normalizeRole(role, roleId)) {
    "administrador" -> 3L
    "doctor" -> 2L
    else -> 1L
}

fun UsuarioResponseDto.toUsuarioDto(): UsuarioDto {
    val displayName = listOfNotNull(nombre, apellido).joinToString(" ").trim()
    val roleText = extractRoleText(rol) ?: rolInfo?.nombre
    val roleIdFromRol = extractRoleId(rol)
    val normalizedRole = normalizeRole(roleText, idRol ?: rolInfo?.id ?: roleIdFromRol)
    val workerInfo = doctor ?: trabajador
    return UsuarioDto(
        id = id,
        name = if (displayName.isBlank()) correo.orEmpty() else displayName,
        lastName = apellido,
        email = correo.orEmpty(),
        phone = telefono.orEmpty(),
        password = "",
        profileImageUrl = imagenPerfil,
        role = normalizedRole,
        roleId = idRol ?: rolInfo?.id ?: roleIdFromRol,
        specialty = null,
        salary = workerInfo?.sueldo?.toDouble(),
        birthDate = fechaNacimiento,
        doctorId = workerInfo?.id
    )
}

fun List<UsuarioResponseDto>.toUsuarioDtoList(): List<UsuarioDto> = map { it.toUsuarioDto() }

fun DoctorDto.toUsuarioDto(): UsuarioDto {
    val u = usuario
    val displayName = listOfNotNull(
        u?.nombre ?: nombre,
        u?.apellido ?: apellido
    ).joinToString(" ").trim()
    val rawRole = extractRoleText(u?.rol) ?: tipo
    val resolvedRole = normalizeRole(rawRole, u?.idRol ?: idRol ?: extractRoleId(u?.rol))
    val emailValue = u?.correo ?: correo
    val phoneValue = u?.telefono ?: telefono
    return UsuarioDto(
        id = u?.id ?: id ?: 0,
        name = if (displayName.isBlank()) emailValue.orEmpty() else displayName,
        lastName = u?.apellido ?: apellido,
        email = emailValue.orEmpty(),
        phone = phoneValue.orEmpty(),
        password = "",
        profileImageUrl = u?.imagenPerfil,
        role = resolvedRole,
        roleId = u?.idRol ?: idRol,
        specialty = especialidad,
        salary = sueldo?.toDouble(),
        birthDate = u?.fechaNacimiento ?: fechaNacimiento,
        doctorId = this.id ?: u?.doctor?.id
    )
}

private fun extractRoleText(role: Any?): String? = when (role) {
    is String -> role
    is Number -> role.toLong().toString()
    is RolRequest -> role.nombre ?: role.id.toString()
    is Map<*, *> -> {
        val idCandidate = (role["idRol"] ?: role["id_rol"] ?: role["id"]) as? Number
        val nameCandidate = (role["nombre"] ?: role["rol"] ?: role["role"]) as? String
        nameCandidate ?: idCandidate?.toLong()?.toString()
    }
    else -> null
}

private fun extractRoleId(role: Any?): Long? = when (role) {
    is RolRequest -> role.id
    is Number -> role.toLong()
    is Map<*, *> -> {
        val idAny = role["idRol"] ?: role["id_rol"] ?: role["id"]
        (idAny as? Number)?.toLong()
    }
    else -> null
}
