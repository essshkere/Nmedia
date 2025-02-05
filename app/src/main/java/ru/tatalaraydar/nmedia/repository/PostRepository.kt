package ru.tatalaraydar.nmedia.repository

import ru.tatalaraydar.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun save(post: Post, callback: PostRepository.CustomCallback<Unit>)
    fun removeById(id: Long,callback: PostRepository.CustomCallback<Unit>)
    fun likeById(post: Post, callback: PostRepository.CustomCallback<Post>): Post
    fun updateShareById(id: Long)

    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface CustomCallback<T> {
        fun onSuccess(result: T)
        fun onError(error: Throwable)
    }
}