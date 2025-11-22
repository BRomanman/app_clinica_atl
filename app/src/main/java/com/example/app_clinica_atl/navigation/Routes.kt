package com.example.app_clinica_atl.navigation

sealed class Route(val path: String) {
    object Login : Route("login")
    object Register : Route("register")
    object Home : Route("home")
    object PatientProfile : Route("patient_profile")
    object Seguros : Route("seguros")
    data object ContratarSeguro : Route("contratarSeguro/{id}") {
        fun create(id: Long) = "contratarSeguro/$id"
    }
    object BookAppointment : Route("book_appointment")
    object LogoutConfirmation : Route("logout_confirmation")

    // --- Rutas de Doctor ---
    object DoctorMenu : Route("doctor_menu")
    object DoctorSchedule : Route("doctor_schedule")
    object DoctorSearchPatient : Route("doctor_search_patient")

    object DoctorProfile : Route("doctor_profile/{doctorId}") {
        fun createRoute(doctorId: Long) = "doctor_profile/$doctorId"
    }
    object DoctorPreview : Route("doctor_preview/{doctorId}") {
        fun createRoute(doctorId: Long) = "doctor_preview/$doctorId"
    }
    object DoctorPatientProfile : Route("doctor_patient_profile/{patientId}") {
        fun createRoute(patientId: Long) = "doctor_patient_profile/$patientId"
    }

    // --- Rutas de Admin ---
    object AdminMenu : Route("admin_menu")
    object AdminAddSpecialty : Route("admin_add_specialty")
    object AdminAddDoctor : Route("admin_add_doctor")

    // Ruta para VER la lista de doctores (La que pediste restaurar)
    object AdminViewDoctors : Route("admin_view_doctors")

    // Ruta para "Mis Datos" del Admin (Solo texto, sin cámara)
    object AdminProfile : Route("admin_profile")

    // Ruta para Editar un Doctor específico (al hacer clic en la lista)
    object AdminEditDoctor : Route("admin_edit_doctor/{doctorId}") {
        fun createRoute(id: Long) = "admin_edit_doctor/$id"
    }

    // Ruta de utilidad para reinicio
    object Restart : Route("restart_app")
}