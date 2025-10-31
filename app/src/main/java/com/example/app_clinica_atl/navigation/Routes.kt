package com.example.app_clinica_atl.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Login : Route("login")
    data object Register : Route("register")
    data object BookAppointment : Route("book_appointment")
    data object Insurance : Route("insurance")
    data object InsuranceForm : Route("insurance_form")
    data object Profile : Route("profile")
    data object PatientSearch : Route("patient_search")
    data object DoctorMenu : Route("doctor_menu")
    data object DoctorAppointments : Route("doctor_appointments")
    data object DoctorProfile : Route("doctor_profile")
    data object AdminMenu : Route("admin_menu")
    data object AdminDoctorSchedule : Route("admin_doctor_schedule")
    data object AdminUserHistories : Route("admin_user_histories")
    data object AdminDoctorSearch : Route("admin_doctor_search")
    data object AdminManageDoctor : Route("admin_manage_doctor")
    data object AdminAddDoctor : Route("admin_add_doctor")
}
