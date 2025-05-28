package ru.tatalaraydar.nmedia.dto

import ru.tatalaraydar.nmedia.entity.AttachmentType

sealed class FeedItem{
    abstract val id: Long
}

data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
) : FeedItem()

data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val share: Int,
    val views_post: Int,
) : FeedItem()

data class Attachment(
    val url: String,
    val type: AttachmentType,
)

