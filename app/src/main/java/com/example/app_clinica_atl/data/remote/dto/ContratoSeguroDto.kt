package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName


data class ContratoSeguroDto(
    @SerializedName("id") val id: Long,
    @SerializedName(value = "idSeguro", alternate = ["id_seguro"]) val idSeguro: Long,
    @SerializedName(value = "idUsuario", alternate = ["id_usuario"]) val idUsuario: Long,
    @SerializedName(value = "rutBeneficiarios", alternate = ["rut_beneficiarios"]) val rutBeneficiarios: String,
    @SerializedName(value = "nombreBeneficiarios", alternate = ["nombre_beneficiarios"]) val nombreBeneficiarios: String,
    @SerializedName(value = "apellidoBeneficiarios", alternate = ["apellido_beneficiarios"]) val apellidoBeneficiarios: String,
    @SerializedName(value = "fechaNacimientoBeneficiarios", alternate = ["fecha_nacimiento_beneficiarios"]) val fechaNacimientoBeneficiarios: String,
    @SerializedName(value = "correoContacto", alternate = ["correo_contacto"]) val correoContacto: String,
    @SerializedName(value = "telefonoContacto", alternate = ["telefono_contacto"]) val telefonoContacto: String,
    @SerializedName(value = "metodoPago", alternate = ["metodo_pago"]) val metodoPago: String,
    @SerializedName(value = "fechaContratacion", alternate = ["fecha_contratacion"]) val fechaContratacion: String,
    @SerializedName(value = "fechaCancelacion", alternate = ["fecha_cancelacion"]) val fechaCancelacion: String,
    @SerializedName("estado") val estado: String

)
