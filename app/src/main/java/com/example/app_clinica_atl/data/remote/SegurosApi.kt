package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SegurosApi {

    @GET("insurances")
    suspend fun getSeguros(): List<SeguroDto>

    @GET("insurances/{id}")
    suspend fun getSeguroById(@Path("id") id: Long): SeguroDto

    @POST("insurances")
    suspend fun createSeguro(@Body seguro: SeguroDto): SeguroDto

    @PUT("insurances/{id}")
    suspend fun updateSeguro(
        @Path("id") id: Long,
        @Body seguro: SeguroDto
    ): SeguroDto

    @DELETE("insurances/{id}")
    suspend fun deleteSeguro(@Path("id") id: Long): Response<Unit>
}
