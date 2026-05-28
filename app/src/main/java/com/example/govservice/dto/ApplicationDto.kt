package com.example.govservice.dto

data class CreateApplicationRequest(
    val serviceId: Int,
    val applicantFullName: String,
    val birthDate: String,
    val passportData: String,
    val address: String,
    val purpose: String
)

data class ApplicationResponse(
    val id: Int,
    val userId: Int,
    val serviceId: Int,
    val serviceTitle: String,
    val status: String,
    val applicantFullName: String,
    val birthDate: String,
    val passportData: String,
    val address: String,
    val purpose: String,
    val employeeComment: String?,
    val createdAt: String,
    val updatedAt: String
)

data class UpdateStatusRequest(
    val status: String,
    val employeeComment: String? = null
)