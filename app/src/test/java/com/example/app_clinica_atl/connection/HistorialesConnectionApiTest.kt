package com.example.app_clinica_atl.connection

import com.example.app_clinica_atl.data.remote.HistorialesApi
import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

/**
 * Verifica que la llamada de HistorialesApi responda HTTP 200 (simulada).
 */
class HistorialesConnectionApiTest {

    private val api: HistorialesApi = mockk()

    private val sample = HistorialDto(
        id = 3L,
        idUsuario = 5L,
        fechaConsulta = "2024-02-01T14:00:00",
        diagnostico = "Gripe",
        observaciones = "Reposo"
    )

    @Test
    fun `getHistoriales returns list`() = runBlocking {
        coEvery { api.getHistoriales() } returns listOf(sample)
        val result = api.getHistoriales()
        assertEquals(1, result.size)
        assertEquals("Gripe", result.first().diagnostico)
    }

    @Test
    fun `getHistorialById returns item`() = runBlocking {
        coEvery { api.getHistorialById(3L) } returns sample
        val result = api.getHistorialById(3L)
        assertEquals(3L, result.id)
    }

    @Test
    fun `createHistorial returns created item`() = runBlocking {
        coEvery { api.createHistorial(sample) } returns sample.copy(id = 4L)
        val result = api.createHistorial(sample)
        assertEquals(4L, result.id)
    }

    @Test
    fun `updateHistorial returns updated item`() = runBlocking {
        coEvery { api.updateHistorial(3L, sample.copy(diagnostico = "Resfrio")) } returns sample.copy(diagnostico = "Resfrio")
        val result = api.updateHistorial(3L, sample.copy(diagnostico = "Resfrio"))
        assertEquals("Resfrio", result.diagnostico)
    }

    @Test
    fun `deleteHistorial returns HTTP 200`() = runBlocking {
        coEvery { api.deleteHistorial(3L) } returns Response.success(Unit)
        val response = api.deleteHistorial(3L)
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
    }
}