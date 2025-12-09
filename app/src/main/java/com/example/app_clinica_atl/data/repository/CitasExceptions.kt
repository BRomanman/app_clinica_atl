package com.example.app_clinica_atl.data.repository

class SlotAlreadyTakenException(message: String = "Esta hora ya fue tomada.") : Exception(message)

class CitaNotFoundException(message: String = "No se encontró la cita seleccionada.") : Exception(message)
