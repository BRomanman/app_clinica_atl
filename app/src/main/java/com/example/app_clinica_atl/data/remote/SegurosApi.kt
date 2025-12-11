package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.ContratoSeguroDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SegurosApi {

    // --- Seguros ---
    @GET("seguros")
    suspend fun getSeguros(): Response<List<SeguroDto>>

    @GET("seguros/{id}")
    suspend fun getSeguroById(@Path("id") id: Long): Response<SeguroDto>

    @POST("seguros")
    suspend fun createSeguro(@Body seguro: SeguroDto): Response<SeguroDto>

    @PUT("seguros/{id}")
    suspend fun updateSeguro(
        @Path("id") id: Long,
        @Body seguro: SeguroDto
    ): Response<SeguroDto>

    @DELETE("seguros/{id}")
    suspend fun deleteSeguro(@Path("id") id: Long): Response<Unit>

    // --- Contratos ---
    @GET("seguros/contratos/usuario/{idUsuario}")
    suspend fun contratosPorUsuario(@Path("idUsuario") idUsuario: Long): Response<List<ContratoSeguroDto>>

    @GET("seguros/contratos/seguro/{idSeguro}")
    suspend fun contratosPorSeguro(@Path("idSeguro") idSeguro: Long): Response<List<ContratoSeguroDto>>

    @GET("seguros/contratos/{idContrato}")
    suspend fun contratoById(@Path("idContrato") idContrato: Long): Response<ContratoSeguroDto>

    @POST("seguros/contratos")
    suspend fun crearContrato(@Body contrato: ContratoSeguroDto): Response<ContratoSeguroDto>

    @POST("seguros/contratos/{id}/cancelar")
    suspend fun cancelarContrato(@Path("id") idContrato: Long): Response<ContratoSeguroDto>
}
