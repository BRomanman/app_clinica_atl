package com.example.app_clinica_atl.data.repositoryTest

import com.example.app_clinica_atl.data.local.cita.CitaDao
import com.example.app_clinica_atl.data.repository.CitasRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CitasRepositoryImplTest {

    private val appointmentDao: CitaDao = mockk()
    private val repository = CitasRepositoryImpl(appointmentDao)

    @Test
    fun `getBookedTimes returns success list`() = runBlocking {
        coEvery { appointmentDao.getBookedTimesForDoctorOnDate(1L, "2024-01-01") } returns listOf("09:00")

        val result = repository.getBookedTimes(1L, "2024-01-01")

        assertTrue(result.isSuccess)
        assertEquals(listOf("09:00"), result.getOrNull())
    }
}
