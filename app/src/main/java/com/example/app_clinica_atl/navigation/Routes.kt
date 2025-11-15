package com.example.app_clinica_atl.navigation

/**
 * Define todas las rutas de navegación de la aplicación.
 */
sealed class Route(val path: String) {
    object Login : Route("login")
    object Register : Route("register")
    object Home : Route("home")
    object PatientProfile : Route("patient_profile")
    object Seguros : Route("seguros")
    object BookAppointment : Route("book_appointment")

    // Rutas de Doctor
    object DoctorMenu : Route("doctor_menu")
    object DoctorSchedule : Route("doctor_schedule")
    object DoctorSearchPatient : Route("doctor_search_patient") // <-- ¡¡RUTA AÑADIDA!!

    // Ruta de Perfil de Doctor (con argumento)
    object DoctorProfile : Route("doctor_profile/{doctorId}") {
        fun createRoute(doctorId: Long) = "doctor_profile/$doctorId"
    }

    // --- Rutas de Admin ---
    object AdminMenu : Route("admin_menu")
    object AdminAddSpecialty : Route("admin_add_specialty")
    object AdminAddDoctor : Route("admin_add_doctor")
}