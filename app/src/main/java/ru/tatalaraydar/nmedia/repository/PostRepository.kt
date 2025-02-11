package ru.tatalaraydar.nmedia.repository

import ru.tatalaraydar.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun save(post: Post, callback: Callback<Post>)
    fun removeById(id: Long, callback: Callback<Unit>)
    fun likeById(post: Post, callback: Callback<Post>)

    fun updateShareById(id: Long)

    fun getAllAsync(callback: Callback<List<Post>>)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface CustomCallback<T> {
        fun onSuccess(result: T)
        fun onError(error: Throwable)
    }

    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }
}





