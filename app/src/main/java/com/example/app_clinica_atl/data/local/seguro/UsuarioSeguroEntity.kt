package com.example.app_clinica_atl.data.local.seguro

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity
import com.example.app_clinica_atl.data.local.seguro.SeguroEntity

/**
 * Define la tabla "user_insurance_table".
 * Esta es una tabla de unión que registra qué paciente (patientId)
 * tiene qué seguro (insuranceId) y cuál es su estado (status).
 */
@Entity(
    tableName = "user_insurance_table",
    indices = [Index("patientId"), Index("insuranceId")],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SeguroEntity::class,
            parentColumns = ["id"],
            childColumns = ["insuranceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UsuarioSeguroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val insuranceId: Long,
    val status: String // "activo" o "cancelado"
)
