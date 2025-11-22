package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioSeguroDto
import com.example.app_clinica_atl.data.remote.dto.ContratoSeguroDto
import com.example.app_clinica_atl.ui.screen.BeneficiarioForm
import kotlinx.coroutines.flow.Flow

interface SegurosRepository {

    fun getAvailableInsurances(): Flow<List<SeguroDto>>

    fun getActiveSubscription(patientId: Long): Flow<UsuarioSeguroDto?>

    fun getActiveSubscriptionDetails(patientId: Long): Flow<SeguroDto?>

    suspend fun getInsurancesForPatient(patientId: Long): Result<List<SeguroDto>>

    suspend fun subscribeToInsurance(patientId: Long, insuranceId: Long): Result<Unit>

    suspend fun cancelSubscription(subscriptionId: Long): Result<Unit>

    // NUEVO Y REAL: crear contratos uno por beneficiario
    suspend fun crearContrato(contrato: ContratoSeguroDto): Result<Unit>
}
