package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequestDto(
    @SerializedName("currentPassword")
    val currentPassword: String,
    @SerializedName("newPassword")
    val newPassword: String
)
