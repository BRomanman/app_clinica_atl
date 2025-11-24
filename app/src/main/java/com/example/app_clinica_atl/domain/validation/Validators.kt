package com.example.app_clinica_atl.domain.validation

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

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
@RequiresApi(Build.VERSION_CODES.O)
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
}

/**
 * Valida que un texto sea un email válido.
 * Usa un patrón simple independiente de android.util.Patterns para evitar NPE en tests JVM.
 */
fun validateEmail(email: String): String? {
    if (email.isBlank()) {
        return "Email es requerido."
    }
    if (!SIMPLE_EMAIL_REGEX.matcher(email).matches()) {
        return "Email no es válido."
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

fun validateChileanPhoneNumber(phone: String): String? {
    val phoneRegex = Regex("^\\+569\\d{8}$")

    if (phone.isBlank()) {
        return "Teléfono es requerido."
    }
    if (phone.length > 12) {
        return "Teléfono debe tener 12 caracteres (+569########)."
    }
    if (!phone.matches(phoneRegex)) {
        return "Formato no válido. (Ej: +56912345678)"
    }
    return null
}

// Patrón de email compatible con tests JVM (ASCII).
private val SIMPLE_EMAIL_REGEX: Pattern = Pattern.compile(
    "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\$",
    Pattern.CASE_INSENSITIVE
)
