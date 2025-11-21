package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.CitaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CitasApi {

    @GET("citas")
    suspend fun getAppointments(): Response<List<CitaDto>>

    @GET("citas/{id}")
    suspend fun getAppointmentById(@Path("id") id: Long): Response<CitaDto>

    @GET("citas/usuario/{idUsuario}")
    suspend fun getAppointmentsByUser(@Path("idUsuario") userId: Long): Response<List<CitaDto>>

    @GET("citas/usuario/{idUsuario}/proximas")
    suspend fun getUpcomingAppointmentsByUser(@Path("idUsuario") userId: Long): Response<List<CitaDto>>

    @GET("citas/doctor/{idDoctor}/fecha/{fecha}")
    suspend fun getAppointmentsByDoctorAndDate(
        @Path("idDoctor") doctorId: Long,
        @Path("fecha") dateIso: String // Formato yyyy-MM-dd
    ): Response<List<CitaDto>>

    @POST("citas")
    suspend fun createAppointment(@Body cita: CitaDto): Response<CitaDto>

    @PUT("citas/{id}")
    suspend fun updateAppointment(
        @Path("id") id: Long,
        @Body cita: CitaDto
    ): Response<CitaDto>
}
