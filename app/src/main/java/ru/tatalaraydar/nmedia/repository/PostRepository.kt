package ru.tatalaraydar.nmedia.repository

import ru.tatalaraydar.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun save(post: Post)
    fun removeById(id: Long)
    fun likeById(post: Post): Post
    fun updateShareById(id: Long)

    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }
}