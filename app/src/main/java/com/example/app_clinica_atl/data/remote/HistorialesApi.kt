package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HistorialesApi {

    @GET("usuario/{usuarioId}")
    suspend fun getHistorialByUserId(@Path("usuarioId") userId: Long): Response<List<HistorialDto>>

    @GET("doctor/{doctorId}")
    suspend fun getHistorialByDoctorId(@Path("doctorId") doctorId: Long): Response<List<HistorialDto>>

    @GET("{historialId}")
    suspend fun getHistorialById(@Path("historialId") id: Long): Response<HistorialDto>
}
