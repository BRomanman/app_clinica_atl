package com.example.app_clinica_atl.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LoginTest {

    @Test
    fun `login email must be valid`() {
        assertEquals("Email es requerido.", validateEmail(""))
        assertEquals("Email no es válido.", validateEmail("invalid"))
        assertNull(validateEmail("user@atl.cl"))
    }

    @Test
    fun `login password cannot be blank`() {
        assertEquals("Contraseña es requerida.", validateLoginPassword(""))
        assertNull(validateLoginPassword("secreta123"))
    }
}
