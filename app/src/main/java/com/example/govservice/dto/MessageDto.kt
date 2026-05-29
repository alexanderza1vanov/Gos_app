package com.example.govservice.dto

data class ChatMessageResponse(
    val id: Int,
    val applicationId: Int,
    val senderId: Int,
    val senderRole: String,
    val text: String,
    val createdAt: String
)

data class SendMessageRequest(
    val text: String
)