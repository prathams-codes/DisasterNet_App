package com.example.disasternet.data

data class DisasterMessage(
    val messageId: String,
    val senderName: String,
    val message: String,
    val timestamp: Long,
    val isSentByMe: Boolean
)