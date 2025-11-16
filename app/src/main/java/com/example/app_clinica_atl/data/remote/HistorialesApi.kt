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

    @GET("medical-records")
    suspend fun getHistoriales(): List<HistorialDto>

    @GET("medical-records/{id}")
    suspend fun getHistorialById(@Path("id") id: Long): HistorialDto

    @POST("medical-records")
    suspend fun createHistorial(@Body historial: HistorialDto): HistorialDto

    @PUT("medical-records/{id}")
    suspend fun updateHistorial(
        @Path("id") id: Long,
        @Body historial: HistorialDto
    ): HistorialDto

    @DELETE("medical-records/{id}")
    suspend fun deleteHistorial(@Path("id") id: Long): Response<Unit>
}
