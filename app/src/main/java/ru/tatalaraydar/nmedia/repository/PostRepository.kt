package ru.tatalaraydar.nmedia.repository

import androidx.lifecycle.LiveData
import ru.tatalaraydar.nmedia.dto.Post

interface PostRepository {

    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)

//    fun getAllAsync(callback: Callback<List<Post>>)
//    fun save(post: Post, callback: Callback<Post>)
//    fun removeById(id: Long, callback: Callback<Unit>)
//    fun likeById(post: Post, callback: Callback<Post>)

    // fun getAll(): List<Post>
    fun updateShareById(id: Long)

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





