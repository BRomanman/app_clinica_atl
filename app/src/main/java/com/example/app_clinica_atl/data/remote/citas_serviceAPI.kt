package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.CitasDto
import retrofit2.http.GET

interface `citas_serviceAPI` {
    //todos los endpoints que recibe de la api

    //Este link es de demostracion, no tiene que ir así
    //TODO
    @GET("")


    //IMPLEMENTAR CORUTINAS
    suspend fun obtenerCitas(): List<CitasDto>

}