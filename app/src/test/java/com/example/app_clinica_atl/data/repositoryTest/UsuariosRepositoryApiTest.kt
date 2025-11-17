package com.example.app_clinica_atl.data.repositoryTest

import com.example.app_clinica_atl.data.local.usuario.UsuarioDao
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.UserDto
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifica que el login vía API remota funcione (sin usar Room local).
 */
class UsuariosRepositoryApiTest {

    private val userDao: UsuarioDao = mockk(relaxed = true) // no se usa en este test
    private val usuariosApi: UsuariosApi = mockk()
    private val repository = UsuariosRepository(userDao, usuariosApi)

    @Test
    fun `loginViaApi returns matching remote user`() = runBlocking {
        val remoteUsers = listOf(
            UserDto(
                id = 99L,
                nombre = "Jane",
                apellido = "Doe",
                fechaNacimiento = "1990-01-01",
                correo = "jane@remote.cl",
                telefono = "+56999999999",
                contrasena = "12345",
                idRol = 3L
            )
        )
        coEvery { usuariosApi.getUsers() } returns remoteUsers

        val result = repository.loginViaApi("jane@remote.cl", "12345")

        assertTrue(result.isSuccess)
        assertEquals(99L, result.getOrNull()?.id)
        assertEquals("paciente", result.getOrNull()?.role)
    }
}
