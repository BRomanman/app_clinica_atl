package com.example.app_clinica_atl.domain.validation

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
// --- 1. IMPORTAR LIBRERÍAS DE FECHA ---
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El email es obligatorio"
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if (!ok) "Formato de email inválido" else null
}

fun validateNamePart(name: String, fieldName: String = "El campo"): String? {
    if (name.isBlank()) return "$fieldName es obligatorio"
    // Mantengo la validación de 2-10 caracteres que estaba en tu archivo
    if (name.trim().length < 2) return "$fieldName debe tener al menos 2 letras"
    if (name.trim().length > 10) return "$fieldName no debe exceder los 10 caracteres"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(name)) "Solo letras y espacios" else null
}

fun validatePhoneDigitsOnly(phone: String): String? {
    if (phone.isBlank()) return "El teléfono es obligatorio"
    if (!phone.all { it.isDigit() }) return "Solo números"
    if (phone.length !in 8..15) return "Debe tener entre 8 y 15 dígitos"
    return null
}


fun validateFechaNacimiento(fecha: String): String? {
    if (fecha.isBlank()) return "La fecha es obligatoria"
    // Regex simple para DD-MM-YYYY
    val regex = Regex("^\\d{2}-\\d{2}-\\d{4}$")
    if (!regex.matches(fecha)) return "Formato debe ser DD-MM-YYYY"

    // (Se podrían añadir validaciones de fecha real, ej: que no sea en el futuro)
    // --- 2. MOVER LÓGICA DE PARSEO A LA NUEVA FUNCIÓN ---
    // (Ya no es necesario aquí, la nueva función lo maneja)
    return null
}

// Esta es la validación "Penca" (demasiado estricta) que NO usaremos ahora
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

fun validateSimplePassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria"
    if (pass.length < 6) return "Mínimo 6 caracteres"
    return null
}


fun validateConfirm(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Confirma tu contraseña"
    return if (pass != confirm) "Las contraseñas no coinciden" else null
}

// --- 3. NUEVA FUNCIÓN DE VALIDACIÓN DE EDAD ---
/**
 * Valida que una fecha (en formato DD-MM-YYYY) sea de alguien con al menos [minAge] años.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun validateEdadMinima(fecha: String, minAge: Int): String? {
    // Define el formato que esperamos (DD-MM-YYYY)
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    return try {
        // 1. Convierte el texto a un objeto de Fecha
        val birthDate = LocalDate.parse(fecha, formatter)

        // 2. Obtiene la fecha de hoy
        val today = LocalDate.now()

        // 3. Calcula cuál es la fecha límite (hoy - 18 años)
        // Ej: Si hoy es 09-11-2025, la fecha límite es 09-11-2007
        val minValidDate = today.minusYears(minAge.toLong())

        // 4. Compara
        if (birthDate.isAfter(minValidDate)) {
            // Si la fecha de nacimiento es *después* de la fecha límite,
            // significa que la persona es menor de 18.
            "Debes tener al menos $minAge años para registrarte"
        } else {
            // La fecha es válida (es igual o anterior a la fecha límite)
            null
        }
    } catch (e: DateTimeParseException) {
        // Si la fecha es inválida (ej: "30-02-2000"), falla aquí
        "Fecha inválida"
    }
}