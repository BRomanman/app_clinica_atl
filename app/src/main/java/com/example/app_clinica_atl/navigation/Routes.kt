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
}