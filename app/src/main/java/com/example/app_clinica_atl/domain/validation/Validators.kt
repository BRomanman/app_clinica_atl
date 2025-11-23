package com.example.app_clinica_atl.domain.validation

import android.util.Patterns
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// NOTA: Estas funciones devuelven un 'String?'
// Devuelven 'null' si la validación es exitosa.
// Devuelven un 'String' con el mensaje de error si la validación falla.

/**
 * Valida que un campo de texto no esté vacío.
 */
fun validateRequired(value: String, fieldName: String): String? {
    if (value.isBlank()) {
        return "$fieldName es requerido."
    }
    return null
}

/**
 * Valida que un nombre/apellido solo contenga letras y espacios.
 */
fun validatePersonName(value: String, fieldName: String): String? {
    if (value.isBlank()) {
        return "$fieldName es requerido."
    }
    val nameRegex = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s]+$")
    if (!nameRegex.matches(value.trim())) {
        return "$fieldName solo puede contener letras y espacios (sin números)."
    }
    return null
}

/**
 * Valida que el RUT chileno tenga el formato 12345678-9 (sin puntos).
 */
fun validateRut(value: String): String? {
    if (value.isBlank()) {
        return "RUT es requerido."
    }
    val rutRegex = Regex("^\\d{1,2}\\.?\\d{3}\\.?\\d{3}-[0-9kK]$")
    if (!rutRegex.matches(value)) {
        return "RUT no válido. Usa el formato 12.345.678-9."
    }
    return null
}

/**
 * Valida que la fecha esté en formato dd-mm-aaaa y no sea futura.
 */
fun validateDateDdMmYyyy(value: String, fieldName: String = "Fecha"): String? {
    if (value.isBlank()) {
        return "$fieldName es requerida."
    }
    val dateRegex = Regex("^\\d{2}-\\d{2}-\\d{4}$")
    if (!dateRegex.matches(value)) {
        return "$fieldName debe tener el formato dd-mm-aaaa."
    }
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val parsed = LocalDate.parse(value, formatter)
        if (parsed.isAfter(LocalDate.now())) "$fieldName no puede ser futura." else null
    } catch (e: DateTimeParseException) {
        "$fieldName no es válida."
    }
    return null
}

/**
 * Valida que un texto sea un email válido.
 */
fun validateEmail(email: String): String? {
    if (email.isBlank()) {
        return "Email es requerido."
    }
    val androidPattern = Patterns.EMAIL_ADDRESS
    val matches = androidPattern?.matcher(email)?.matches() ?: SIMPLE_EMAIL_REGEX.matches(email)
    if (!matches) {
        return "Email no es v?lido."
    }
    return null
}

/**
 * Valida la contraseña para la pantalla de Login.
 */
fun validateLoginPassword(password: String): String? {
    if (password.isBlank()) {
        return "Contraseña es requerida."
    }
    return null
}

/**
 * Valida la contraseña para la pantalla de Registro.
 */
fun validateRegisterPassword(password: String, confirm: String): Result<Unit> {
    val passwordErrors = mutableListOf<String>()
    if (password.length < 8) {
        passwordErrors.add("• Al menos 8 caracteres.")
    }
    if (!password.any { it.isDigit() }) {
        passwordErrors.add("• Al menos 1 número.")
    }
    if (!password.any { it.isUpperCase() }) {
        passwordErrors.add("• Al menos 1 mayúscula.")
    }
    if (!password.any { it.isLowerCase() }) {
        passwordErrors.add("• Al menos 1 minúscula.")
    }

    if (passwordErrors.isNotEmpty()) {
        val errorMsg = "Contraseña débil:\n" + passwordErrors.joinToString("\n")
        return Result.failure(Exception(errorMsg))
    }

    if (password != confirm) {
        return Result.failure(Exception("Las contraseñas no coinciden."))
    }

    return Result.success(Unit)
}

// --- ¡¡FUNCIÓN AÑADIDA!! ---
/**
 * Valida que un número de teléfono siga el formato chileno (+569xxxxxxxx).
 */
fun validateChileanPhoneNumber(phone: String): String? {
    // Expresión regular:
    // ^     = inicio de la línea
    // \+569 = debe empezar exactamente con "+569"
    // \d{8} = debe ser seguido por 8 dígitos numéricos
    // $     = fin de la línea
    val phoneRegex = Regex("^\\+569\\d{8}$")

    if (phone.isBlank()) {
        return "Teléfono es requerido."
    }
    if (!phone.matches(phoneRegex)) {
        return "Formato no válido. (Ej: +56912345678)"
    }
    return null
}

// Fallback para tests JVM donde Patterns.EMAIL_ADDRESS puede ser null.
private val SIMPLE_EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
