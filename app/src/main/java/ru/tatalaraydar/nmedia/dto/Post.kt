package ru.tatalaraydar.nmedia.dto

import ru.tatalaraydar.nmedia.entity.AttachmentType

data class Post (
    val id: Long = 0,
    val author : String = "",
    val published: String = "",
    val authorAvatar :String = "",
    val content: String = "",
    var likes : Int = 999,
    var share : Int = 110,
    val views_post : Int = 1_000_000,
    var likedByMe: Boolean = false,
    val videoURL: String = "",
    val isVisible: Boolean = true,
    var attachment: Attachment? = null,
) {
    fun getFullImageUrl() = attachment?.url?.let {
        "http://10.0.2.2:9999/media/$it"
    }
}

data class Attachment(
    val url: String,
    val type: AttachmentType,
)

