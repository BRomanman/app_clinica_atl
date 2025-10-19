package com.example.app_clinica_atl.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val nombre: String,           // <-- CAMBIO: renombrado de 'name'
    val apellido: String,         // <-- CAMBIO: campo nuevo
    val fecha_nacimiento: String, // <-- CAMBIO: campo nuevo (guardado como String ISO "YYYY-MM-DD")
    val email: String,
    val phone: String,
    val password: String,
    val id_rol: Long              // <-- CAMBIO: campo nuevo
)