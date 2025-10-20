package com.example.app_clinica_atl.data.model

data class Patient(
    val id: String,
    val nombre: String,
    val direccion: String,
    val numeroContacto: String,
    val correo: String,
    val historialMedico: String
)