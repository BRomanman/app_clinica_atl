package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HistorialesApi {

    @GET("historial")
    suspend fun getHistoriales(): List<HistorialDto>

    @GET("historial/{id}")
    suspend fun getHistorialById(@Path("id") id: Long): HistorialDto

    @POST("historial")
    suspend fun createHistorial(@Body historial: HistorialDto): HistorialDto

    @PUT("historial/{id}")
    suspend fun updateHistorial(
        @Path("id") id: Long,
        @Body historial: HistorialDto
    ): HistorialDto

    @DELETE("historial/{id}")
    suspend fun deleteHistorial(@Path("id") id: Long): Response<Unit>
}
