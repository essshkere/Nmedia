package ru.tatalaraydar.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.tatalaraydar.nmedia.dto.Attachment
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
    val videoURL: String = "",
    val isVisible: Boolean = true,
    @Embedded
    var attachment: AttachmentEmbeddable?
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
        videoURL,
        attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.published,
                dto.authorAvatar,
                dto.content,
                dto.likes,
                dto.share,
                dto.views_post,
                dto.likedByMe,
                dto.videoURL,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)



data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}

