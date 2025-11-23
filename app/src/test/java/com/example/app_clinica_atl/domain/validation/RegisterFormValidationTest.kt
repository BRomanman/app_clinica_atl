package com.example.app_clinica_atl.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Validaciones del formulario de Registro.
 */
class RegisterFormValidationTest {

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
    fun `email y telefono cumplen formato`() {
        assertEquals("Email es requerido.", validateEmail(""))
        assertEquals("Email no es valido.", validateEmail("usuario@"))
        assertNull(validateEmail("usuario@atl.cl"))

        assertEquals("Teléfono es requerido.", validateChileanPhoneNumber(""))
        assertEquals("Formato no válido. (Ej: +56912345678)", validateChileanPhoneNumber("+123"))
        assertNull(validateChileanPhoneNumber("+56912345678"))
    }

    @Test
    fun `password valida fuerza y coincidencia`() {
        assertTrue(validateRegisterPassword("abc", "abc").isFailure)        // débil
        assertTrue(validateRegisterPassword("Password1", "Password2").isFailure) // no coincide
        assertTrue(validateRegisterPassword("Password1", "Password1").isSuccess)
    }
}
