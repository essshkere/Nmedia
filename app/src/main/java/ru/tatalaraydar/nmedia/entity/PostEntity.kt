package ru.tatalaraydar.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.tatalaraydar.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val published: String,
    val authorAvatar: String,
    val content: String,
    var likes: Int = 999,
    var share: Int = 110,
    val views_post: Int = 1_000_000,
    var likedByMe: Boolean,
    val videoURL: String = ""
) {
    fun toDto() = Post(
        id,
        author,
        published,
        authorAvatar,
        content,
        likes,
        share,
        views_post,
        likedByMe,
        videoURL
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.published,
                dto. authorAvatar,
                dto. content,
                dto. likes,
                dto. share,
                dto. views_post,
                dto.likedByMe,
                dto.videoURL
            )

    }
}