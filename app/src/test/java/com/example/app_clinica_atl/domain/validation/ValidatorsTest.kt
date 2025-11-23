package com.example.app_clinica_atl.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    @Test
    fun `validateRequired fails on blank and passes on text`() {
        assertEquals("Nombre es requerido.", validateRequired("   ", "Nombre"))
        assertNull(validateRequired("John", "Nombre"))
    }

    @Test
    fun `validateEmail detects missing and invalid`() {
        assertEquals("Email es requerido.", validateEmail(""))
        assertEquals("Email no es valido.", validateEmail("sadolksdm"))
        assertNull(validateEmail("user@example.com"))
    }

    @Test
    fun `validateLoginPassword requires non blank`() {
        assertEquals("Contraseña es requerida.", validateLoginPassword(""))
        assertNull(validateLoginPassword("secret"))
    }

    @Test
    fun `validateRegisterPassword enforces strength and match`() {
        val weak = validateRegisterPassword("abc", "abc")
        assertTrue(weak.isFailure)
        val mismatch = validateRegisterPassword("Password1", "Password2")
        assertTrue(mismatch.isFailure)
        val ok = validateRegisterPassword("Password1", "Password1")
        assertTrue(ok.isSuccess)
    }

    @Test
    fun `validateChileanPhoneNumber enforces format`() {
        assertEquals("Teléfono es requerido.", validateChileanPhoneNumber(""))
        assertEquals("Formato no válido. (Ej: +56912345678)", validateChileanPhoneNumber("+123"))
        assertNull(validateChileanPhoneNumber("+56912345678"))
    }

    @Test
    fun `all validators pass with correct data`() {
        assertNull(validateRequired("Nombre Apellido", "Nombre"))
        assertNull(validateEmail("ok" +
                "@correo.cl"))
        assertNull(validateLoginPassword("pass123"))
        assertTrue(validateRegisterPassword("Password1", "Password1").isSuccess)
        assertNull(validateChileanPhoneNumber("+56987654321"))
    }
}
