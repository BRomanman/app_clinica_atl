package com.example.app_clinica_atl.data.local.cita

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity

/**
 * Entidad para la tabla de Citas.
 * Ahora incluye 'doctorId' y 'status'.
 */
@Entity(
    tableName = "appointments",
    // Índices para mejorar el rendimiento de las consultas
    indices = [Index("patientId"), Index("doctorId")],
    // Foreign Keys (relaciones) para asegurar la integridad de los datos
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE // Si se borra un usuario, se borran sus citas
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE // Si se borra un doctor, se borran sus citas
        )
    ]
)
data class CitaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val patientId: Long, // <-- ID del paciente (UsuarioEntity)

    // --- ESTAS SON LAS COLUMNAS QUE FALTABAN ---

    /**
     * ID del doctor (UsuarioEntity) con el que se agenda.
     * ESTA es la columna que faltaba y causaba el error de KSP.
     */
    val doctorId: Long,

    val date: String,    // "YYYY-MM-DD"
    val time: String,    // "HH:MM"

    /**
     * Estado de la cita (ej: "agendada", "cancelada", "completada")
     */
    val status: String
)
