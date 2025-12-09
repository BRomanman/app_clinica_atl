package com.example.app_clinica_atl.domain.specialty

import java.text.Normalizer

object SpecialtyCatalog {

    val officialSpecialties = listOf(
        "Medicina General",
        "Cirugia General",
        "Dermatologia",
        "Neurologia",
        "Oftalmologia",
        "Cardiologia",
        "Gastroenterologia",
        "Radiologia",
        "Oncologia",
        "Infectologia"
    )

    private val normalizedSet = officialSpecialties
        .map { normalize(it) }
        .toSet()

    fun isOfficial(name: String?): Boolean =
        name?.let { normalizedSet.contains(normalize(it)) } == true

    fun canonicalName(name: String?): String? =
        name?.let { candidate ->
            officialSpecialties.firstOrNull { normalize(it) == normalize(candidate) }
        }

    private fun normalize(value: String): String {
        val trimmed = value.trim()
        val normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
        return normalized.replace("\\p{M}".toRegex(), "").lowercase()
    }
}
