package ru.tatalaraydar.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.tatalaraydar.nmedia.dto.Media
import ru.tatalaraydar.nmedia.dto.MediaUpload
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.dto.PushToken

interface PostRepository {

    suspend fun clearAll()
    val data: Flow<PagingData<Post>>
    suspend fun likeById(id: Long)
    suspend fun makeAllPostsVisible()
    suspend fun removeById(id: Long)
    suspend fun save(post: Post, upload: MediaUpload?)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun sendPushToken(token: PushToken)
    suspend fun upload(upload: MediaUpload): Media
    fun updateShareById(id: Long)


}