package ru.tatalaraydar.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.tatalaraydar.nmedia.dto.Attachment
import ru.tatalaraydar.nmedia.dto.Post


data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(
                url = it.url.substringAfterLast("/"), //todo сохранил только имя файла
                type = it.type
            )
        }
    }
}


fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)


@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long,
    val published: Long,
    val authorAvatar: String,
    val content: String,
    var likes: Int,
    var share: Int,
    val views_post: Int,
    var likedByMe: Boolean,
    val videoURL: String = "",
    val isVisible: Boolean = true,
    @Embedded
    var attachment: AttachmentEmbeddable? = null,
    val ownedByMe: Boolean
) {
    fun toDto() = Post(
        id = id,
        author = author,
        authorId = authorId,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likes = likes,
        share = share,
        views_post = views_post,
        likedByMe = likedByMe,
        ownedByMe = ownedByMe,
        attachment = attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post) = PostEntity(
            id = dto.id,
            author = dto.author,
            authorId = dto.authorId,
            authorAvatar = dto.authorAvatar,
            content = dto.content,
            published = dto.published,
            likes = dto.likes,
            share = dto.share,
            views_post = dto.views_post,
            likedByMe = dto.likedByMe,
            ownedByMe = dto.ownedByMe,
            attachment = AttachmentEmbeddable.fromDto(dto.attachment)
        )
    }
}

