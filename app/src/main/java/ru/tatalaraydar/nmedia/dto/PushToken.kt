package ru.tatalaraydar.nmedia.dto

data class PushToken(
    val token: String,
    val recipientId: Long? = null
)
