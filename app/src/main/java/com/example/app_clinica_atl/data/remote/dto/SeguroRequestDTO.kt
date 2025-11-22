package com.example.app_clinica_atl.data.remote.dto

data class ContratoSeguroRequest(
    val id_usuario: Long,
    val idSeguro: Long,
    val beneficiarios: List<BeneficiarioRequest>,
    val metodo_pago: String,
    val estado: String
)

data class BeneficiarioRequest(
    val nombre: String,
    val apellido: String,
    val rut: String,
    val fecha_nacimiento: String
)