package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import io.mockk.coEvery
import io.mockk.mockk

@OptIn(ExperimentalCoroutinesApi::class)
class SpecialtyRepositoryImplTest {

    private val api: UsuariosApi = mockk()

    @Test
    fun `getAllSpecialties devuelve lista limpia y filtra nombres en blanco`() = runTest {
        coEvery { api.getAllSpecialties() } returns Response.success(
            listOf(
                EspecialidadResponseDto(id = 1, nombre = "Cardiologia"),
                EspecialidadResponseDto(id = 2, nombre = "  "), // debe filtrarse
                EspecialidadResponseDto(id = 3, nombre = "Traumatologia")
            )
        )

        val repo = SpecialtyRepositoryImpl(api)
        val result = repo.getAllSpecialties().first()

        assertEquals(listOf("Cardiologia", "Traumatologia"), result.map { it.name })
    }

    @Test(expected = HttpException::class)
    fun `getAllSpecialties lanza HttpException si la respuesta es error`() = runTest {
        val errorBody = "boom".toResponseBody("application/json".toMediaType())
        coEvery { api.getAllSpecialties() } returns Response.error(500, errorBody)

        val repo = SpecialtyRepositoryImpl(api)
        repo.getAllSpecialties().first() // debe lanzar
    }

    @Test
    fun `createSpecialty success retorna dto mapeado`() = runTest {
        coEvery { api.createSpecialty(any()) } returns Response.success(
            EspecialidadResponseDto(id = 10, nombre = "Oncologia", doctorId = 99)
        )
        val repo = SpecialtyRepositoryImpl(api)
        val res = repo.createSpecialty(EspecialidadRequestDto(nombre = "Oncologia", doctorId = 99L))
        assertTrue(res.isSuccess)
        assertEquals("Oncologia", res.getOrThrow().name)
        assertEquals(10L, res.getOrThrow().id)
    }

    @Test
    fun `createSpecialty falla si el backend responde vacio o sin nombre`() = runTest {
        coEvery { api.createSpecialty(any()) } returns Response.success(
            EspecialidadResponseDto(id = 1, nombre = "   ")
        )
        val repo = SpecialtyRepositoryImpl(api)
        val res = repo.createSpecialty(EspecialidadRequestDto(nombre = "  ", doctorId = 1L))
        assertTrue(res.isFailure)
    }

    @Test
    fun `createSpecialty con error http devuelve failure`() = runTest {
        val errorBody = "bad".toResponseBody("application/json".toMediaType())
        coEvery { api.createSpecialty(any()) } returns Response.error(400, errorBody)

        val repo = SpecialtyRepositoryImpl(api)
        val res = repo.createSpecialty(EspecialidadRequestDto(nombre = "Test", doctorId = 1L))

        assertTrue(res.isFailure)
    }
}
