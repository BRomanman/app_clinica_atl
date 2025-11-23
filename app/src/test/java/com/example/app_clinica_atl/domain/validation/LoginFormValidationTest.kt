package com.example.app_clinica_atl.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Validaciones del formulario de Login.
 */
class LoginFormValidationTest {

    @Test
    fun `email requerido e invalido`() {
        assertEquals("Email es requerido.", validateEmail(""))
        assertEquals("Email no es válido.", validateEmail("correo@"))
    }

    @Test
    fun `email valido limpia error`() {
        assertNull(validateEmail("user@atl.cl"))
    }

    @Test
    fun `password requerida`() {
        assertEquals("Contraseña es requerida.", validateLoginPassword(""))
        assertNull(validateLoginPassword("Secreta123"))
    }
}
