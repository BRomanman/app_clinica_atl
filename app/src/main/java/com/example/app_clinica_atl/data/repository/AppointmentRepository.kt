package com.example.app_clinica_atl.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.app_clinica_atl.data.local.appointment.AppointmentDao
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
import com.example.app_clinica_atl.data.model.DoctorInfo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AppointmentRepository(
    // --- 1. INYECTAR EL DAO REAL ---
    private val appointmentDao: AppointmentDao
) {

    // --- 2. LISTA DE DOCTORES CORREGIDA ---
    // (Ahora creamos objetos DoctorInfo "reales" que coinciden con data/model/DoctorInfo.kt)
    // (Solo rellenamos los campos que la UI necesita: firstName, lastName, specialty, since)
    private val doctorsByDepartment = mapOf(
        "Medicina General" to listOf(
            DoctorInfo(
                id = "doc_001",
                firstName = "Víctor",
                lastName = "Rosendo",
                specialty = "Medicina General",
                since = "2010" // 'since' es un String en tu data class
            ),
            DoctorInfo(
                id = "doc_002",
                firstName = "María",
                lastName = "González",
                specialty = "Medicina General",
                since = "2015"
            ),
            DoctorInfo(
                id = "doc_003",
                firstName = "Luis",
                lastName = "Martínez",
                specialty = "Medicina General",
                since = "2018"
            )
        ),
        "Cardiología" to listOf(
            DoctorInfo(
                id = "doc_004",
                firstName = "Ana",
                lastName = "Fernández",
                specialty = "Cardiología",
                since = "2005"
            ),
            DoctorInfo(
                id = "doc_005",
                firstName = "Carlos",
                lastName = "Pérez",
                specialty = "Cardiología",
                since = "2012"
            )
        ),
        "Dermatología" to listOf(
            DoctorInfo(
                id = "doc_006",
                firstName = "Sofía",
                lastName = "López",
                specialty = "Dermatología",
                since = "2017"
            ),
            DoctorInfo(
                id = "doc_007",
                firstName = "Jorge",
                lastName = "Díaz",
                specialty = "Dermatología",
                since = "2019"
            )
        ),
        "Pediatría" to listOf(
            DoctorInfo(
                id = "doc_008",
                firstName = "Laura",
                lastName = "Gómez",
                specialty = "Pediatría",
                since = "2014"
            ),
            DoctorInfo(
                id = "doc_009",
                firstName = "Miguel",
                lastName = "Torres",
                specialty = "Pediatría",
                since = "2020"
            )
        )
    )
    // --- FIN DEL CAMBIO ---

    fun getDoctorsByDepartment(department: String): List<DoctorInfo> {
        return doctorsByDepartment[department] ?: emptyList()
    }

    // --- 3. NUEVA FUNCIÓN QUE LA UI NECESITA ---
    // (BookAppointmentViewModel la necesita para los menús desplegables)
    fun getDepartments(): List<String> {
        return doctorsByDepartment.keys.toList()
    }
    // ---

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAvailableSlots(date: LocalDate, doctor: DoctorInfo): List<LocalTime> {
        // (La lógica de horas disponibles sigue siendo mockeada, lo cual está bien para aprender)
        return listOf(
            LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0),
            LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30),
            LocalTime.of(14, 0), LocalTime.of(14, 30), LocalTime.of(15, 0)
        )
    }

    // --- 2. FUNCIÓN REAL PARA GUARDAR CITAS ---
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveAppointment(
        patientId: Long,
        doctorName: String,
        department: String,
        date: LocalDate,
        time: LocalTime
    ) {
        val appointment = AppointmentEntity(
            patientId = patientId,
            doctorName = doctorName,
            department = department,
            // Guardamos como texto simple
            date = date.format(DateTimeFormatter.ISO_LOCAL_DATE), // "2025-11-20"
            time = time.format(DateTimeFormatter.ISO_LOCAL_TIME)  // "10:30:00"
        )
        appointmentDao.insertAppointment(appointment)
    }

    // --- 3. FUNCIÓN REAL PARA LEER CITAS ---
    fun getAppointmentsForUser(patientId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getAppointmentsForUser(patientId)
    }

    // --- 4. FUNCIÓN REAL PARA BORRAR CITAS ---
    suspend fun deleteAppointment(appointmentId: Long) {
        appointmentDao.deleteAppointmentById(appointmentId)
    }
}