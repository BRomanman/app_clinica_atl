package com.example.app_clinica_atl.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app_clinica_atl.data.local.appointment.AppointmentDao
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity

// (Esta migración la hicimos para añadir la columna de migración, pero quedó obsoleta por la 5_6)
// (La dejamos por historial por si acaso, pero la 5_6 es la que importa para isLoggedIn)
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Esta migración fue reemplazada por la 5_6 que reconstruye la tabla.
        // Si migramos de 2 a 6 directamente, no pasa nada.
        // Si migramos de 2 a 3, añade la columna.
        db.execSQL("ALTER TABLE users ADD COLUMN IF NOT EXISTS isLoggedIn INTEGER NOT NULL DEFAULT 0")
    }
}

// Migración para añadir campos de doctor y paciente a las citas
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE appointments ADD COLUMN patient_name TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE appointments ADD COLUMN doctor_id TEXT NOT NULL DEFAULT ''")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_appointments_doctor_id` ON `appointments` (`doctor_id`)")
    }
}

// Migración para añadir photoUri a los usuarios
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE users ADD COLUMN photoUri TEXT")
    }
}

// Migración para añadir isLoggedIn a los usuarios (la forma robusta)
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Crear la nueva tabla con todas las columnas (incluyendo isLoggedIn)
        db.execSQL("""
            CREATE TABLE users_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                fecha_nacimiento TEXT NOT NULL,
                email TEXT NOT NULL,
                phone TEXT NOT NULL,
                password TEXT NOT NULL,
                id_rol INTEGER NOT NULL,
                photoUri TEXT,
                isLoggedIn INTEGER NOT NULL DEFAULT 0
            )
        """)

        // 2. Copiar los datos de la tabla vieja (v5) a la nueva (v6)
        db.execSQL("""
            INSERT INTO users_new (id, nombre, apellido, fecha_nacimiento, email, phone, password, id_rol, photoUri)
            SELECT id, nombre, apellido, fecha_nacimiento, email, phone, password, id_rol, photoUri FROM users
        """)

        // 3. Borrar la tabla vieja
        db.execSQL("DROP TABLE users")

        // 4. Renombrar la tabla nueva
        db.execSQL("ALTER TABLE users_new RENAME TO users")

        // 5. Re-crear el índice único en el email
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_users_email` ON `users` (`email`)")
    }
}


@Database(
    entities = [
        UserEntity::class,
        AppointmentEntity::class
    ],
    version = 6, // Estamos en la versión 6
    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
    // Cambiamos 'true' a 'false' para que Room no busque
    // los archivos .json que nos faltan (3.json, 4.json, 5.json, 6.json)
    exportSchema = false
    // --- FIN DE LA CORRECCIÓN ---
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun appointmentDao(): AppointmentDao
}