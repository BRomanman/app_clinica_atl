package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RemoteModule
import com.example.app_clinica_atl.data.remote.citas_serviceAPI
import com.example.app_clinica_atl.data.remote.dto.CitasDto

class CitasRepository(
    private val api: citas_serviceAPI = RemoteModule.create(citas_serviceAPI::class.java)
){
    suspend fun fetchCitas(): Result<List<CitasDto>>{
        try{
            val data = api.obtenerCitas()
            Result.success(data)

        }catch(e: Exception){
            Result.failure(e)
        }

        return TODO("Provide the return value")
    }
}