package com.example.app_clinica_atl.data.remote.citas

import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.remote.dto.ReservarCitaRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface CitasApiService {

    @GET("api/v1/citas/doctor/{idDoctor}/fecha/{fecha}")
    suspend fun getCitasDoctorFecha(
        @Path("idDoctor") doctorId: Long,
        @Path("fecha") fecha: String
    ): List<CitaDto>

    @PATCH("api/v1/citas/{id}/reservar")
    suspend fun reservarCita(
        @Path("id") citaId: Long,
        @Body request: ReservarCitaRequest
    ): CitaDto

    @GET("api/v1/citas/usuario/{idUsuario}/proximas")
    suspend fun getProximasCitasByUsuario(
        @Path("idUsuario") userId: Long
    ): List<CitaDto>

    @PATCH("api/v1/citas/{id}/cancelar")
    suspend fun cancelarCita(
        @Path("id") citaId: Long
    ): CitaDto

    @GET("api/v1/citas/doctor/{idDoctor}/proximas")
    suspend fun getProximasCitasByDoctor(
        @Path("idDoctor") doctorId: Long
    ): List<CitaDto>

    @PUT("api/v1/citas/{id}")
    suspend fun actualizarCita(
        @Path("id") citaId: Long,
        @Body request: UpdateCitaEstadoRequest
    ): CitaDto
}
