package ru.tatalaraydar.nmedia.dao

import ru.tatalaraydar.nmedia.dto.Post

interface PostDao {
    fun getAll(): List<Post>
    fun save(post: Post): Post
    fun updateLikeById(id: Long)
    fun removeById(id: Long)
}
