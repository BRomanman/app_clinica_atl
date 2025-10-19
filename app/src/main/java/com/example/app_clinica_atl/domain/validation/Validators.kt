package com.example.app_clinica_atl.domain.validation

import android.util.Patterns

fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El email es obligatorio"
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if (!ok) "Formato de email inválido" else null
}

// --- CAMBIO: Renombrado de 'validateNameLettersOnly' a 'validateNamePart' ---
// (Lo usaremos para nombre y apellido)
fun validateNamePart(name: String, fieldName: String = "El campo"): String? {
    if (name.isBlank()) return "$fieldName es obligatorio"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(name)) "Solo letras y espacios" else null
}

fun validatePhoneDigitsOnly(phone: String): String? {
    if (phone.isBlank()) return "El teléfono es obligatorio"
    if (!phone.all { it.isDigit() }) return "Solo números"
    if (phone.length !in 8..15) return "Debe tener entre 8 y 15 dígitos"
    return null
}

// --- CAMBIO: Nuevo validador para Fecha de Nacimiento ---
fun validateFechaNacimiento(fecha: String): String? {
    if (fecha.isBlank()) return "La fecha es obligatoria"
    // Regex simple para YYYY-MM-DD
    val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    if (!regex.matches(fecha)) return "Formato debe ser YYYY-MM-DD"
    // (Se podrían añadir validaciones de fecha real, ej: que no sea en el futuro)
    return null
}
// --- FIN DE CAMBIO ---

fun validateStrongPassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria"
    if (pass.length < 8) return "Mínimo 8 caracteres"
    if (!pass.any { it.isUpperCase() }) return "Debe incluir una mayúscula"
    if (!pass.any { it.isLowerCase() }) return "Debe incluir una minúscula"
    if (!pass.any { it.isDigit() }) return "Debe incluir un número"
    if (!pass.any { !it.isLetterOrDigit() }) return "Debe incluir un símbolo"
    if (pass.contains(' ')) return "No debe contener espacios"
    return null
}

fun validateConfirm(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Confirma tu contraseña"
    return if (pass != confirm) "Las contraseñas no coinciden" else null
}