package com.example.app_clinica_atl.data.repositoryTest

import com.example.app_clinica_atl.data.remote.CitasApi
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Prueba simple del cliente CitasApi: simula la respuesta remota y valida campos clave.
 */
class CitasRepositoryApiTest {

    private val citasApi: CitasApi = mockk()

    @Test
    fun `getAppointments returns remote data`() = runBlocking {
        val remote = listOf(
            CitaDto(
                id = 10L,
                fechaCita = "2024-01-01T10:00:00",
                estado = "agendada",
                idUsuario = 5L,
                idDoctor = 7L,
                idPago = null,
                idReceta = null,
                idResena = null,
                idResumen = null,
                idConsulta = null
            )
        )
        coEvery { citasApi.getAppointments() } returns remote

        val result = citasApi.getAppointments()

        assertEquals(1, result.size)
        assertEquals(7L, result.first().idDoctor)
        assertEquals("agendada", result.first().estado)
    }
}
