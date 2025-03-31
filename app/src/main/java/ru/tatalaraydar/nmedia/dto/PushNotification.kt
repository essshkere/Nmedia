
package ru.tatalaraydar.nmedia.dto



data class NotificationContent(
    val title: String?,
    val body: String
)

data class PushData(
    val recipientId: Long?,
    val content: NotificationContent
)
