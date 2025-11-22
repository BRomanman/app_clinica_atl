package com.example.app_clinica_atl.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterTest {

    @Test
    fun `required fields must not be blank`() {
        assertEquals("Nombre es requerido.", validateRequired("", "Nombre"))
        assertNull(validateRequired("Ana", "Nombre"))
    }

    @Test
    fun `register password enforces strength and match`() {
        val weak = validateRegisterPassword("abc", "abc")
        assertTrue(weak.isFailure)

        val mismatch = validateRegisterPassword("Password1", "Password2")
        assertTrue(mismatch.isFailure)

        val ok = validateRegisterPassword("Password1", "Password1")
        assertTrue(ok.isSuccess)
    }
}
