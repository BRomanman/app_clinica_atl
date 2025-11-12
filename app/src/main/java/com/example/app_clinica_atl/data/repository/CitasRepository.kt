package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RemoteModule
import com.example.app_clinica_atl.data.remote.CitasServiceAPI
import com.example.app_clinica_atl.data.remote.dto.CitasDto

class CitasRepository(
    private val api: CitasServiceAPI = RemoteModule.create(CitasServiceAPI::class.java)
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