package com.example.app_clinica_atl.data.repositoryTest

import com.example.app_clinica_atl.data.remote.SegurosApi
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Prueba simple del cliente SegurosApi: simula respuesta remota y valida contenido.
 */
class SegurosRepositoryApiTest {

    private val segurosApi: SegurosApi = mockk()

    @Test
    fun `getSeguros returns remote data`() = runBlocking {
        val remote = listOf(
            SeguroDto(
                id = 2L,
                nombreSeguro = "Plan Familiar",
                descripcion = "Cobertura completa",
                idUsuario = 5L,
                fechaCreacion = "2024-03-10T12:00:00"
            )
        )
        coEvery { segurosApi.getSeguros() } returns remote

        val result = segurosApi.getSeguros()

        assertEquals(1, result.size)
        assertEquals("Plan Familiar", result.first().nombreSeguro)
        assertEquals(5L, result.first().idUsuario)
    }
}
