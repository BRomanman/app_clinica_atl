package com.example.app_clinica_atl.data.local.insurance

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Define la tabla "insurance_table" en la base de datos.
 * Guardará los tipos de seguros disponibles (ej: Básico, Premium).
 */
@Entity(
    tableName = "insurance_table",
    indices = [Index(value = ["name"], unique = true)]
)
data class InsuranceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double
)