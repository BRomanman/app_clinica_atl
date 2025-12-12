package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.remote.dto.ReservarCitaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface CitasApi {

    @GET("citas/{id}")
    suspend fun getCitaById(
        @Path("id") id: Long
    ): CitaDto

    @GET("citas/usuario/{idUsuario}")
    suspend fun getCitasByUsuario(
        @Path("idUsuario") idUsuario: Long
    ): Response<List<CitaDto>>

    @GET("citas/usuario/{idUsuario}/proximas")
    suspend fun getProximasCitasUsuario(
        @Path("idUsuario") idUsuario: Long
    ): Response<List<CitaDto>>

    @GET("citas/doctor/{idDoctor}/proximas")
    suspend fun getProximasCitasDoctor(
        @Path("idDoctor") idDoctor: Long
    ): Response<List<CitaDto>>

    @GET("citas/doctor/{idDoctor}/fecha/{fecha}")
    suspend fun getCitasPorDoctorYFecha(
        @Path("idDoctor") idDoctor: Long,
        @Path("fecha") fecha: String // formato yyyy-MM-dd
    ): Response<List<CitaDto>>

    @PATCH("citas/{id}/cancelar")
    suspend fun cancelarCita(
        @Path("id") id: Long
    ): Response<CitaDto>

    @PUT("citas/{id}/reservar")
    suspend fun reservarCita(
        @Path("id") id: Long,
        @Body request: ReservarCitaRequest
    ): Response<CitaDto>

    @PUT("citas/{id}")
    suspend fun updateCita(
        @Path("id") id: Long,
        @Body cita: CitaDto
    ): CitaDto
}
