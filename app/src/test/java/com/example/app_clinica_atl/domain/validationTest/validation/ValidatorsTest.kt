package com.example.app_clinica_atl.domain.validationTest.validation

import com.example.app_clinica_atl.domain.validation.validateEmail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])//Todo si las pruebas están dando error hay que hacer un downgrade a sdk 34

class ValidatorsTest {

    //El nombre debe tener un indicio de lo que hace
    @Test
    fun validateEmail_TestOK(){
        val error = validateEmail("a@a.cl")
        assertNull(error)
    }

    @Test
    fun validateEmail_TestNull(){
        val error = validateEmail("")
        assertEquals("El email es obligatorio", error)
    }

    //todo terminar todos los test para el archivo de validators
    @Test
    fun (){


    }

}