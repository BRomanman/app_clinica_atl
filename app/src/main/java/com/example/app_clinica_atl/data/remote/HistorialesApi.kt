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

    @GET("historial/{historialId}")
    suspend fun getHistorialById(@Path("historialId") id: Long): HistorialDto

    @GET("historial/usuario/{usuarioId}")
    suspend fun getHistorialByUserId(@Path("usuarioId") userId: Long): List<HistorialDto>

}
