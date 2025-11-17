package com.example.app_clinica_atl.connection

import com.example.app_clinica_atl.data.remote.CitasApi
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response


class CitasConnectionApiTest {

    private val api: CitasApi = mockk()

    private val sample = CitaDto(
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

    @Test
    fun `getAppointments returns list`() = runBlocking {
        coEvery { api.getAppointments() } returns listOf(sample)
        val result = api.getAppointments()
        assertEquals(1, result.size)
        assertEquals(7L, result.first().idDoctor)
    }

    @Test
    fun `getAppointmentById returns appointment`() = runBlocking {
        coEvery { api.getAppointmentById(10L) } returns sample
        val result = api.getAppointmentById(10L)
        assertEquals(10L, result.id)
    }

    @Test
    fun `createAppointment returns created appointment`() = runBlocking {
        coEvery { api.createAppointment(sample) } returns sample.copy(id = 11L)
        val result = api.createAppointment(sample)
        assertEquals(11L, result.id)
    }

    @Test
    fun `updateAppointment returns updated appointment`() = runBlocking {
        coEvery { api.updateAppointment(10L, sample.copy(estado = "confirmada")) } returns sample.copy(estado = "confirmada")
        val result = api.updateAppointment(10L, sample.copy(estado = "confirmada"))
        assertEquals("confirmada", result.estado)
    }

    @Test
    fun `deleteAppointment returns HTTP 200`() = runBlocking {
        coEvery { api.deleteAppointment(10L) } returns Response.success(Unit)
        val response = api.deleteAppointment(10L)
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
    }
}
