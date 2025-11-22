package com.example.app_clinica_atl.data.remote.dto

import com.google.gson.annotations.SerializedName


data class ContratoSeguroDto(
    @SerializedName("id") val id: Long,
    @SerializedName("idSeguro") val idSeguro: Long,
    @SerializedName("id_usuario") val idUsuario: Long,
    @SerializedName("rut_beneficiarios") val rut_beneficiarios: String,
    @SerializedName("nombre_beneficiarios") val nombre_beneficiarios: String,
    @SerializedName("apellido_beneficiarios") val apellido_beneficiarios: String,
    @SerializedName("fecha_nacimiento_beneficiarios") val fecha_nacimiento_beneficiarios: String,
    @SerializedName("correo_contacto") val correo_contacto: String,
    @SerializedName("telefono_contacto") val telefono_contacto: String,
    @SerializedName("metodo_pago") val metodo_pago: String,
    @SerializedName("fecha_contratacion") val fecha_contratacion: String,
    @SerializedName("fecha_cancelacion") val fecha_cancelacion: String,
    @SerializedName("estado") val estado: String

)