package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.CitaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CitasApi {

    @GET("appointments")
    suspend fun getAppointments(): List<CitaDto>

    @GET("appointments/{id}")
    suspend fun getAppointmentById(@Path("id") id: Long): CitaDto

    @POST("appointments")
    suspend fun createAppointment(@Body cita: CitaDto): CitaDto

    @PUT("appointments/{id}")
    suspend fun updateAppointment(
        @Path("id") id: Long,
        @Body cita: CitaDto
    ): CitaDto

    @DELETE("appointments/{id}")
    suspend fun deleteAppointment(@Path("id") id: Long): Response<Unit>
}
