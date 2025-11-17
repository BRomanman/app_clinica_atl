package com.example.app_clinica_atl.data.local.usuario

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad ÚNICA para todos los usuarios de la app (pacientes, doctores, admins).
 * Reemplaza a los modelos falsos 'DoctorInfo' y 'Patient'.
 */
@Entity(tableName = "user_table")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String, // Añadimos teléfono, es útil para todos los roles
    val password: String,
    val profileImageUrl: String? = null,

    // --- CAMPOS AÑADIDOS ---
    /**
     * Define el tipo de usuario.
     * Puede ser "paciente", "doctor" o "admin".
     */
    val role: String,

    /**
     * Si el rol es "doctor", aquí se almacena su especialidad como un simple String.
     * (Ej: "Cardiología", "Dermatología").
     * Es nullable porque los pacientes y admins no la tienen.
     */
    val specialty: String? = null,

    /**
     * Salario del doctor (nullable, ya que pacientes y admins no lo tienen).
     * Lo añadimos ahora para que la base de datos esté completa.
     */
    val salary: Double? = null
)
