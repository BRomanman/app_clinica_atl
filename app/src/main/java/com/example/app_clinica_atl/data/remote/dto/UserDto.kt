package com.example.app_clinica_atl.data.remote.dto

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val profileImageUrl: String?,
    val role: String,
    val specialty: String?,
    val salary: Double?
)
