package com.example.app_clinica_atl.domain.validationTest.validation

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.domain.validation.validateEmail
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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


    //comprobar que trae datos de la api
    @Test
    fun callUsersEndpoint() = runBlocking {
        val api = RetrofitClient.usuariosApi
        val result = api.getUsers()
        assertTrue(result.isNotEmpty())
    }

}