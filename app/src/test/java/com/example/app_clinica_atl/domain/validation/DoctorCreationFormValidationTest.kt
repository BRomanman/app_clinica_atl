package com.example.app_clinica_atl.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Validaciones usadas en el formulario de creación de doctores (Admin).
 */
class DoctorCreationFormValidationTest {

    @Test
    fun `nombres y apellidos requieren solo letras`() {
        assertEquals("Nombre es requerido.", validatePersonName("", "Nombre"))
        assertEquals("Nombre solo puede contener letras y espacios (sin números).", validatePersonName("Ana1", "Nombre"))
        assertNull(validatePersonName("Ana María", "Nombre"))

        assertEquals("Apellido es requerido.", validatePersonName("", "Apellido"))
        assertEquals("Apellido solo puede contener letras y espacios (sin números).", validatePersonName("P3rez", "Apellido"))
        assertNull(validatePersonName("Pérez Soto", "Apellido"))
    }

    @Test
    fun `email y telefono siguen formato requerido`() {
        assertEquals("Email es requerido.", validateEmail(""))
        assertEquals("Email no es valido.", validateEmail("usuario@"))
        assertNull(validateEmail("doctor@atl.cl"))

        assertEquals("Teléfono es requerido.", validateChileanPhoneNumber(""))
        assertEquals("Formato no válido. (Ej: +56912345678)", validateChileanPhoneNumber("+111"))
        assertNull(validateChileanPhoneNumber("+56987654321"))
    }

    @Test
    fun `salario debe ser numero positivo`() {
        assertEquals("Salario inválido", validateSalary(""))
        assertEquals("Salario inválido", validateSalary("0"))
        assertEquals("Salario inválido", validateSalary("-1000"))
        assertEquals("Salario inválido", validateSalary("abc"))
        assertNull(validateSalary("1500000"))
        // soporta separadores comunes tal como el formateo del ViewModel
        assertNull(validateSalary("1.500.000"))
        assertNull(validateSalary("1 500 000"))
        assertNull(validateSalary("1,500,000"))
    }

    @Test
    fun `debe seleccionarse al menos una especialidad`() {
        assertEquals("Debe seleccionar al menos una especialidad.", validateSpecialties(emptyList()))
        assertNull(validateSpecialties(listOf("Cardiología")))
    }
}

// Helpers locales que replican las reglas del ViewModel de AdminAddDoctor
private fun validateSalary(raw: String): String? {
    val clean = raw.replace("\\s".toRegex(), "").replace(".", "").replace(",", "")
    val value = clean.toLongOrNull()
    return if (value == null || value <= 0) "Salario inválido" else null
}

private fun validateSpecialties(list: List<String>): String? =
    if (list.isEmpty()) "Debe seleccionar al menos una especialidad." else null
