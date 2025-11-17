package com.example.app_clinica_atl.domain.validation

import android.util.Patterns

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
 * Valida que un texto sea un email válido.
 */
fun validateEmail(email: String): String? {
    if (email.isBlank()) {
        return "Email es requerido."
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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