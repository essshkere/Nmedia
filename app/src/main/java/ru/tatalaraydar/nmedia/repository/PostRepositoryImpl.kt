package ru.tatalaraydar.nmedia.repository

import android.annotation.SuppressLint
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.tatalaraydar.nmedia.api.ApiService
import ru.tatalaraydar.nmedia.dao.PostDao
import ru.tatalaraydar.nmedia.dao.PostRemoteKeyDao
import ru.tatalaraydar.nmedia.db.AppDb
import ru.tatalaraydar.nmedia.dto.Attachment
import ru.tatalaraydar.nmedia.dto.Media
import ru.tatalaraydar.nmedia.dto.MediaUpload
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.dto.PushToken
import ru.tatalaraydar.nmedia.entity.AttachmentType
import ru.tatalaraydar.nmedia.entity.PostEntity
import ru.tatalaraydar.nmedia.error.ApiError
import ru.tatalaraydar.nmedia.error.NetworkError
import ru.tatalaraydar.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    appDb: AppDb,
    private val postDao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    private val apiService: ApiService,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(
            pageSize = 25,
            prefetchDistance = 10,
            enablePlaceholders = false
        ),
        remoteMediator = PostRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override suspend fun clearAll() {
        postDao.clearAll()
    }
    override suspend fun removeById(id: Long) {
        try {
            postDao.removeById(id)
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            postDao.updateLikeById(id)
            val response = apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            postDao.updateLikeById(id)
            throw NetworkError
        } catch (e: Exception) {
            postDao.updateLikeById(id)
            throw UnknownError
        }
    }

    override suspend fun makeAllPostsVisible() {
        postDao.makeAllPostsVisible()
    }

    override suspend fun save(post: Post, upload: MediaUpload?) {
        try {
            val postWithAttachment = upload?.let { media ->
                val uploadedMedia = upload(media)
                post.copy(attachment = Attachment(uploadedMedia.id, AttachmentType.IMAGE))
            } ?: post

            val response = apiService.save(postWithAttachment)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())

            response.body()?.let { savedPost ->
                postDao.insert(PostEntity.fromDto(savedPost))
            } ?: throw ApiError(response.code(), "Empty response body")
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) = save(post, upload)


    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )
            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun sendPushToken(token: PushToken) {
        try {
            val response = apiService.save(token)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun updateShareById(id: Long) {

    }

    companion object {
        @SuppressLint("DefaultLocale")
        fun formatCount(count: Int): String {
            return when {
                count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
                count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
                else -> count.toString()
            }.replace(",", ".")
        }
    }
}