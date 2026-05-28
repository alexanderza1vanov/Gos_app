package com.example.govservice.dto

data class ServiceResponse(
    val id: Int,
    val title: String,
    val description: String,
    val requiredDocuments: String
)