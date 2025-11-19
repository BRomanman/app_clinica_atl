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

    @GET("citas")
    suspend fun getAppointments(): List<CitaDto>

    @GET("citas/{id}")
    suspend fun getAppointmentById(@Path("id") id: Long): CitaDto

    @POST("citas")
    suspend fun createAppointment(@Body cita: CitaDto): CitaDto

    @PUT("citas/{id}")
    suspend fun updateAppointment(
        @Path("id") id: Long,
        @Body cita: CitaDto
    ): CitaDto

    @DELETE("citas/{id}")
    suspend fun deleteAppointment(@Path("id") id: Long): Response<Unit>
}
