package ru.tatalaraydar.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import ru.tatalaraydar.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow
import ru.tatalaraydar.nmedia.dto.Media
import ru.tatalaraydar.nmedia.dto.MediaUpload
import ru.tatalaraydar.nmedia.dto.PushToken

interface PostRepository {


    val data: Flow<PagingData<Post>>
    suspend fun save(post: Post,upload: MediaUpload?)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    fun updateShareById(id: Long)
    suspend fun makeAllPostsVisible()
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun sendPushToken(token: PushToken)
    suspend fun clearAll()
    fun getNewerCount(id: Long): Flow<Int>

    suspend fun getAll()
}