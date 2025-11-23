package com.example.app_clinica_atl.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Validaciones del formulario de contratación de seguros:
 * datos de contacto y beneficiarios.
 */
class InsuranceContractFormValidationTest {

    @Test
    fun `datos de contacto requieren email y telefono validos`() {
        assertEquals("Email es requerido.", validateEmail(""))
        assertEquals("Email no es valido.", validateEmail("correo@"))
        assertNull(validateEmail("contacto@atl.cl"))

        assertEquals("Teléfono es requerido.", validateChileanPhoneNumber(""))
        assertEquals("Formato no válido. (Ej: +56912345678)", validateChileanPhoneNumber("+111"))
        assertNull(validateChileanPhoneNumber("+56998765432"))
    }

    @Test
    fun `beneficiarios requieren nombre apellido y rut valido`() {
        assertEquals("Nombre es requerido.", validatePersonName("", "Nombre"))
        assertEquals("Nombre solo puede contener letras y espacios (sin números).", validatePersonName("Ana1", "Nombre"))
        assertNull(validatePersonName("Ana", "Nombre"))

        assertEquals("Apellido es requerido.", validatePersonName("", "Apellido"))
        assertNull(validatePersonName("García", "Apellido"))

        assertEquals("RUT es requerido.", validateRut(""))
        assertEquals("RUT no válido. Usa el formato 12.345.678-9.", validateRut("12345678"))
        assertNull(validateRut("12.345.678-5"))
    }

    @Test
    fun `fecha de nacimiento en formato dd-mm-aaaa y no futura`() {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val future = LocalDate.now().plusDays(1).format(formatter)
        val past = LocalDate.now().minusYears(10).format(formatter)

        assertEquals("Fecha es requerida.", validateDateDdMmYyyy(""))
        assertEquals("Fecha debe tener el formato dd-mm-aaaa.", validateDateDdMmYyyy("2024/01/01"))
        assertEquals("Fecha no puede ser futura.", validateDateDdMmYyyy(future))
        assertNull(validateDateDdMmYyyy(past))
    }
}
