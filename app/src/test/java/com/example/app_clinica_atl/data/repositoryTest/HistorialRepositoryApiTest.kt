package com.example.app_clinica_atl.data.repositoryTest

import com.example.app_clinica_atl.data.remote.HistorialesApi
import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Prueba simple del cliente HistorialesApi: simula respuesta remota y valida contenido.
 */
class HistorialRepositoryApiTest {

    private val historialesApi: HistorialesApi = mockk()

    @Test
    fun `getHistoriales returns remote data`() = runBlocking {
        val remote = listOf(
            HistorialDto(
                id = 3L,
                idUsuario = 5L,
                fechaConsulta = "2024-02-01T14:00:00",
                diagnostico = "Gripe",
                observaciones = "Reposo"
            )
        )
        coEvery { historialesApi.getHistoriales() } returns remote

        val result = historialesApi.getHistoriales()

        assertEquals(1, result.size)
        assertEquals("Gripe", result.first().diagnostico)
        assertEquals(5L, result.first().idUsuario)
    }
}
