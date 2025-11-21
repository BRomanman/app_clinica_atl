package com.example.app_clinica_atl.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface HistorialesApi {

    @GET("historial")
    suspend fun getHistoriales(): retrofit2.Response<ResponseBody>

    @GET("historial/{historialId}")
    suspend fun getHistorialById(@Path("historialId") id: Long): retrofit2.Response<ResponseBody>

    @GET("historial/usuario/{usuarioId}")
    suspend fun getHistorialByUserId(@Path("usuarioId") userId: Long): retrofit2.Response<ResponseBody>

    @GET("historial/doctor/{doctorId}")
    suspend fun getHistorialByDoctorId(@Path("doctorId") doctorId: Long): retrofit2.Response<ResponseBody>
}
