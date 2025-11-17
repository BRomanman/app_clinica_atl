package com.example.app_clinica_atl.data.local.especialidad

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Define la tabla "specialty_table" en la base de datos.
 * Guardará las especialidades que el Admin cree.
 */
@Entity(
    tableName = "specialty_table",
    // Creamos un "índice único" para que no se puedan repetir nombres de especialidades
    indices = [Index(value = ["name"], unique = true)]
)
data class EspecialidadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Double // El precio de la consulta para esta especialidad
)
