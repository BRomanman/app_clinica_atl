package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.remote.dto.ReservarCitaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface CitasApi {

    @GET("api/v1/citas/{id}")
    suspend fun getCitaById(
        @Path("id") id: Long
    ): CitaDto

    @GET("api/v1/citas/usuario/{idUsuario}")
    suspend fun getCitasByUsuario(
        @Path("idUsuario") idUsuario: Long
    ): List<CitaDto>

    @GET("api/v1/citas/usuario/{idUsuario}/proximas")
    suspend fun getProximasCitasUsuario(
        @Path("idUsuario") idUsuario: Long
    ): List<CitaDto>

    @GET("api/v1/citas/doctor/{idDoctor}/proximas")
    suspend fun getProximasCitasDoctor(
        @Path("idDoctor") idDoctor: Long
    ): List<CitaDto>

    @GET("/api/v1/citas/doctor/{idDoctor}/fecha/{fecha}")
    suspend fun getCitasPorDoctorYFecha(
        @Path("idDoctor") idDoctor: Long,
        @Path("fecha") fecha: String // formato yyyy-MM-dd
    ): Response<List<CitaDto>>

    @PATCH("/api/v1/citas/{id}/cancelar")
    suspend fun cancelarCita(
        @Path("id") id: Long
    ): Response<CitaDto>

    @PATCH("/api/v1/citas/{id}/reservar")
    suspend fun reservarCita(
        @Path("id") id: Long,
        @Body request: ReservarCitaRequest
    ): Response<CitaDto>
}
