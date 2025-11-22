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

    @GET("seguros")
    suspend fun getSeguros(): List<SeguroDto>

    @GET("seguros/{id}")
    suspend fun getSeguroById(@Path("id") id: Long): SeguroDto

    @POST("seguros")
    suspend fun createSeguro(@Body seguro: SeguroDto): SeguroDto

    @PUT("seguros/{id}")
    suspend fun updateSeguro(
        @Path("id") id: Long,
        @Body seguro: SeguroDto
    ): SeguroDto

    @DELETE("seguros/{id}")
    suspend fun deleteSeguro(@Path("id") id: Long): Response<Unit>

    @GET("seguros/contratos/usuario/{idUsuario}")
    suspend fun contratosPorUsuario(@Path("idUsuario") idUsuario: Long): List<ContratoSeguroDto>

    @GET("seguros/contratos/seguro/{idSeguro}")
    suspend fun contratosPorSeguro(@Path("idSeguro") idSeguro: Long): List<ContratoSeguroDto>

    @GET("seguros/contratos/{idContrato}")
    suspend fun contratoById(@Path("idContrato") idContrato: Long): ContratoSeguroDto

    // POST REAL DEL BACKEND
    @POST("seguros/contratos")
    suspend fun crearContrato(@Body contrato: ContratoSeguroDto): ContratoSeguroDto

    @POST("seguros/contratos/{id}/cancelar")
    suspend fun cancelarContrato(@Path("id") idContrato: Long): ContratoSeguroDto
}
