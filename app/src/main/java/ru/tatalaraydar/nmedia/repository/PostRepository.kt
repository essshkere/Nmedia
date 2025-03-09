package ru.tatalaraydar.nmedia.repository

import androidx.lifecycle.LiveData
import ru.tatalaraydar.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {

    fun getNewerCount(id: Long): Flow<Int>
    val data: Flow<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    fun updateShareById(id: Long)
     suspend fun makeAllPostsVisible()


//    fun getAllAsync(callback: Callback<List<Post>>)
//    fun save(post: Post, callback: Callback<Post>)
//    fun removeById(id: Long, callback: Callback<Unit>)
//    fun likeById(post: Post, callback: Callback<Post>)

    // fun getAll(): List<Post>


//    interface GetAllCallback {
//        fun onSuccess(posts: List<Post>) {}
//        fun onError(e: Exception) {}
//    }
//
//    interface CustomCallback<T> {
//        fun onSuccess(result: T)
//        fun onError(error: Throwable)
//    }
//
//    interface Callback<T> {
//        fun onSuccess(posts: T) {}
//        fun onError(e: Exception) {}
//    }
}





