package com.example.app_clinica_atl.data.remote.api

import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SpecialtyApi {

    @GET("api/v1/especialidades")
    suspend fun getAll(): List<EspecialidadDto>

    @POST("api/v1/especialidades")
    suspend fun create(@Body body: EspecialidadRequestDto): EspecialidadDto
}
