package com.example.app_clinica_atl.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Validaciones aplicadas en el formulario de perfil de usuario
 * (teléfono y cambio de contraseña).
 */
class UserProfileFormValidationTest {

    @Test
    fun `telefono debe cumplir formato chileno`() {
        assertEquals("Teléfono es requerido.", validateChileanPhoneNumber(""))
        assertEquals("Formato no válido. (Ej: +56912345678)", validateChileanPhoneNumber("+123"))
        assertNull(validateChileanPhoneNumber("+56998765432"))
    }

    @Test
    fun `cambio de password exige fuerza y coincidencia`() {
        val weak = validateRegisterPassword("abc", "abc")
        val mismatch = validateRegisterPassword("Password1", "Password2")
        val ok = validateRegisterPassword("Password1", "Password1")

        assertTrue(weak.isFailure)
        assertTrue(mismatch.isFailure)
        assertTrue(ok.isSuccess)
    }
}
