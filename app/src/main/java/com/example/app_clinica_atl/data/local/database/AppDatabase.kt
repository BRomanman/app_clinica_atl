package com.example.app_clinica_atl.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app_clinica_atl.data.local.appointment.AppointmentDao
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity

// --- 1. DEFINICIÓN DE LA BASE DE DATOS ---
@Database(
    entities = [
        UserEntity::class,
        AppointmentEntity::class  // <-- 2. AÑADIR LA NUEVA TABLA DE CITAS
    ],
    version = 3, // <-- 3. SUBIR LA VERSIÓN A 3 (YA QUE ESTÁS AÑADIENDO UNA TABLA)
    exportSchema = true // Mantener para futuras migraciones
)
abstract class AppDatabase : RoomDatabase() {

    // --- 4. DEFINIR LOS DAOs QUE LA DB PROVEE ---
    abstract fun userDao(): UserDao
    abstract fun appointmentDao(): AppointmentDao // <-- 5. AÑADIR EL NUEVO DAO DE CITAS

}

// --- 6. DEFINIR LA MIGRACIÓN DE 2 A 3 ---
// (Tu MainActivity ya estaba intentando usar esto, pero no estaba definido)
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Este SQL crea la nueva tabla "appointments"
        // Coincide con tu AppointmentEntity.kt
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `appointments` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `patient_id` INTEGER NOT NULL, 
                `doctor_name` TEXT NOT NULL, 
                `department` TEXT NOT NULL, 
                `date` TEXT NOT NULL, 
                `time` TEXT NOT NULL, 
                FOREIGN KEY(`patient_id`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """)
        // Crear un índice para búsquedas rápidas (buena práctica)
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_appointments_patient_id` ON `appointments` (`patient_id`)")
    }
}