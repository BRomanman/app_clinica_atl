package com.example.app_clinica_atl.data.repositoryTest

import com.example.app_clinica_atl.data.remote.CitasApi
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

//todo debe tener las respuestas que recibe de la api

class CitasRerpositoryTest {


    @Test
    fun obtenerCitas_return_list() = runBlocking{
        val api = mockk<CitasApi>()
        val repo = CitasRepository(api)

        //todo modificar esto luego de tener los datos que vienen
        //de la api de citas
        val sample = listOf(CitaDto(1)    )

        coEvery{ api.getCitas() } returns sample
        val result = repo.obtenerCitas()
        assertTrue(result.success)

        //todo modificar segun atributos de la api
        assertEquals(1, result.getOrNull()!![0])
    }


}