package com.example.app_clinica_atl.data.local.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// 1. Define la entidad (tabla) para los usuarios.
@Entity(
    tableName = "users",
    // 2. Creamos un "índice" en la columna 'email' y lo marcamos como 'UNIQUE'.
    //    Esto previene que dos usuarios se registren con el mismo email.
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "apellido")
    val apellido: String,

    @ColumnInfo(name = "fecha_nacimiento")
    val fecha_nacimiento: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "password")
    val password: String,

    // 1 = Paciente, 2 = Doctor, 3 = Admin
    @ColumnInfo(name = "id_rol")
    val id_rol: Long,

    @ColumnInfo(name = "photoUri")
    val photoUri: String? = null,

    // --- ¡NUEVO CAMPO PARA EL LOGIN! ---
    // Room mapea Boolean a INTEGER (0 = false, 1 = true)
    // El valor por defecto 0 (false) es crucial.
    @ColumnInfo(name = "isLoggedIn", defaultValue = "0")
    val isLoggedIn: Boolean = false
    // --- FIN ---
)