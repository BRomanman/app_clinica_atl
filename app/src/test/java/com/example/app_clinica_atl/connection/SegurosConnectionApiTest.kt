package com.example.app_clinica_atl.connection

import com.example.app_clinica_atl.data.remote.SegurosApi
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

/**
 * Verifica que la llamada de SegurosApi responda HTTP 200 (simulada).
 */
class SegurosConnectionApiTest {

    private val api: SegurosApi = mockk()

    private val sample = SeguroDto(
        id = 2L,
        nombreSeguro = "Plan Familiar",
        descripcion = "Cobertura completa",
        idUsuario = 5L,
        fechaCreacion = "2024-03-10T12:00:00"
    )

    @Test
    fun `getSeguros returns list`() = runBlocking {
        coEvery { api.getSeguros() } returns listOf(sample)
        val result = api.getSeguros()
        assertEquals(1, result.size)
        assertEquals("Plan Familiar", result.first().nombreSeguro)
    }

    @Test
    fun `getSeguroById returns item`() = runBlocking {
        coEvery { api.getSeguroById(2L) } returns sample
        val result = api.getSeguroById(2L)
        assertEquals(2L, result.id)
    }

    @Test
    fun `createSeguro returns created item`() = runBlocking {
        coEvery { api.createSeguro(sample) } returns sample.copy(id = 3L)
        val result = api.createSeguro(sample)
        assertEquals(3L, result.id)
    }

    @Test
    fun `updateSeguro returns updated item`() = runBlocking {
        coEvery { api.updateSeguro(2L, sample.copy(nombreSeguro = "Plan Plus")) } returns sample.copy(nombreSeguro = "Plan Plus")
        val result = api.updateSeguro(2L, sample.copy(nombreSeguro = "Plan Plus"))
        assertEquals("Plan Plus", result.nombreSeguro)
    }

    @Test
    fun `deleteSeguro returns HTTP 200`() = runBlocking {
        coEvery { api.deleteSeguro(2L) } returns Response.success(Unit)
        val response = api.deleteSeguro(2L)
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
    }
}