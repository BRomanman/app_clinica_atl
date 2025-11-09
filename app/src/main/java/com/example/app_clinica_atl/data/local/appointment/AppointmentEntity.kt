package com.example.app_clinica_atl.data.local.appointment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.app_clinica_atl.data.local.user.UserEntity

// 1. Define la entidad (tabla) para las citas.
@Entity(
    tableName = "appointments",
    // 2. Creamos una "Foreign Key" (llave foránea) para conectar la cita con el usuario (paciente).
    //    Esto asegura que si un usuario se elimina, sus citas también se eliminen (onDelete = CASCADE).
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // 3. Creamos un "índice" en 'patient_id' para que buscar citas por usuario sea ultra-rápido.
    indices = [Index("patient_id")]
)
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "patient_id")
    val patientId: Long, // El ID del UserEntity (paciente)

    @ColumnInfo(name = "doctor_name")
    val doctorName: String,

    @ColumnInfo(name = "department")
    val department: String,

    @ColumnInfo(name = "date")
    val date: String, // Guardamos la fecha como texto (ej: "2025-11-20")

    @ColumnInfo(name = "time")
    val time: String // Guardamos la hora como texto (ej: "10:30")
)