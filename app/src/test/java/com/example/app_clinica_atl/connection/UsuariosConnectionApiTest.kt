package com.example.app_clinica_atl.connection

import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.UserDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

/**
 * Verifica que la llamada remota responda con HTTP 200 (simulada).
 */
class UsuariosConnectionApiTest {

    private val api: UsuariosApi = mockk()

    private val sample = UserDto(
        id = 5L,
        nombre = "Alice",
        apellido = "Smith",
        fechaNacimiento = "1995-05-01",
        correo = "alice@api.cl",
        telefono = "+56911112222",
        contrasena = "pass",
        idRol = 2L
    )

    @Test
    fun `getUsers returns list`() = runBlocking {
        coEvery { api.getUsers() } returns listOf(sample)
        val result = api.getUsers()
        assertEquals(1, result.size)
        assertEquals("alice@api.cl", result.first().correo)
    }

    @Test
    fun `getUserById returns user`() = runBlocking {
        coEvery { api.getUserById(5L) } returns sample
        val result = api.getUserById(5L)
        assertEquals(5L, result.id)
    }

    @Test
    fun `createUser returns created user`() = runBlocking {
        coEvery { api.createUser(sample) } returns sample.copy(id = 6L)
        val result = api.createUser(sample)
        assertEquals(6L, result.id)
    }

    @Test
    fun `updateUser returns updated user`() = runBlocking {
        coEvery { api.updateUser(5L, sample.copy(nombre = "Jane")) } returns sample.copy(nombre = "Jane")
        val result = api.updateUser(5L, sample.copy(nombre = "Jane"))
        assertEquals("Jane", result.nombre)
    }

    @Test
    fun `deleteUser returns HTTP 200`() = runBlocking {
        coEvery { api.deleteUser(5L) } returns Response.success(Unit)
        val response = api.deleteUser(5L)
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
    }
}