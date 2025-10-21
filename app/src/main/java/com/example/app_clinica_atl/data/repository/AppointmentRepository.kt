package com.example.app_clinica_atl.data.repository

import android.os.Build // Necesario para RequiresApi
import android.util.Log
import androidx.annotation.RequiresApi // Necesario para la anotación
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.ui.viewmodel.AppointmentRequest // Depende del ViewModel
import kotlinx.coroutines.delay
import java.time.LocalTime // Necesario para las horas

// Anotación a nivel de clase porque usamos LocalTime, que requiere API 26 (Oreo)
@RequiresApi(Build.VERSION_CODES.O)
class AppointmentRepository {

    // --- Datos de Doctores (Completos, restaurados) ---
    private val departments = listOf(
        "Medicina General", "Cardiología", "Dermatología",
        "Pediatría", "Psicología", "Nutrición"
    )

    private val doctorsByDepartment = mapOf(
        "Medicina General" to listOf(
            DoctorInfo("Dra. Ana Pérez", "Traumatóloga", 2012),
            DoctorInfo("Dra. Juana Pérez", "Médico de familia", 2010),
            DoctorInfo("Dra. Marcela Ruiz", "Ginecóloga", 2015),
            DoctorInfo("Dra. Alejandra Peña", "Médico de atención primaria", 2011),
            DoctorInfo("Dr. Ignacio Fuentes", "Traumatólogo", 2013)
        ),
        "Cardiología" to listOf(
            DoctorInfo("Dr. Juan Torres", "Cardiólogo", 2012),
            DoctorInfo("Dra. Marcela Ruiz", "Cardióloga", 2015), // Nombre repetido, asumo que es correcto
            DoctorInfo("Dra. Ricarda Gómez", "Cardióloga", 2016),
            DoctorInfo("Dra. Valentina Castro", "Cardióloga", 2013)
        ),
        "Dermatología" to listOf(
            DoctorInfo("Dra. Ana Pérez", "Dermatóloga", 2011), // Nombre repetido, asumo que es correcto
            DoctorInfo("Dr. Nicolás Díaz", "Dermatólogo", 2012),
            DoctorInfo("Dra. Isabel Soto", "Dermatóloga", 2014),
            DoctorInfo("Dr. Paulo Bravo", "Dermatólogo", 2013),
            DoctorInfo("Dra. Lorena Salazar", "Dermatóloga", 2015)
        ),
        "Pediatría" to listOf(
            DoctorInfo("Dr. Gabriel Molina", "Pediatra", 2010),
            DoctorInfo("Dra. Fernanda Morales", "Pediatra", 2011),
            DoctorInfo("Dra. Natalia Carrasco", "Pediatra", 2012)
        ),
        "Psicología" to listOf(
            DoctorInfo("Dr. Sebastián Flores", "Psicólogo", 2013),
            DoctorInfo("Dra. Catalina Reyes", "Psicóloga", 2014),
            DoctorInfo("Dr. Esteban Rivas", "Psicólogo", 2015),
            DoctorInfo("Dr. Marcelo Duarte", "Psicólogo", 2012)
        ),
        "Nutrición" to listOf(
            DoctorInfo("Dra. Verónica Contreras", "Nutrióloga", 2011),
            DoctorInfo("Dr. Felipe Lagos", "Nutriólogo", 2012)
        )
    )

    // --- Horas disponibles ---
    // La anotación @RequiresApi es necesaria aquí porque LocalTime.of requiere API 26+
    private val availableTimes = listOf(
        LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 30),
        LocalTime.of(12, 30), LocalTime.of(14, 0), LocalTime.of(15, 30),
        LocalTime.of(17, 0)
    )

    // --- Funciones para obtener los datos ---
    fun getDepartments(): List<String> = departments
    fun getDoctorsByDepartment(department: String): List<DoctorInfo> = doctorsByDepartment[department].orEmpty()
    fun getAvailableTimes(): List<LocalTime> = availableTimes

    // --- Función para simular el envío de la reserva ---
    suspend fun submitAppointment(request: AppointmentRequest): Result<Boolean> {
        delay(1500) // Simula espera de red
        // Imprime en Logcat para verificar
        Log.d("AppointmentRepository", "Reserva enviada (simulado): ${request.doctor.name} - ${request.date} ${request.time}")
        // Simulamos que siempre funciona
        return Result.success(true)
    }
}