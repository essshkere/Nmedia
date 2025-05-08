package ru.tatalaraydar.nmedia.repository

import android.annotation.SuppressLint
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.tatalaraydar.nmedia.api.ApiService
import ru.tatalaraydar.nmedia.dao.PostDao
import ru.tatalaraydar.nmedia.dto.Attachment
import ru.tatalaraydar.nmedia.dto.Media
import ru.tatalaraydar.nmedia.dto.MediaUpload
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.dto.PushToken
import ru.tatalaraydar.nmedia.entity.AttachmentType
import ru.tatalaraydar.nmedia.entity.PostEntity
import ru.tatalaraydar.nmedia.entity.toEntity
import ru.tatalaraydar.nmedia.error.ApiError
import ru.tatalaraydar.nmedia.error.AppError
import ru.tatalaraydar.nmedia.error.NetworkError
import ru.tatalaraydar.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: ApiService
) : PostRepository {

    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = { PostPagingSource(apiService) },
    ).flow

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())

            val body = response.body() ?: throw ApiError(response.code(), response.message())


            dao.clearAll()
            body.forEach { dao.insert(PostEntity.fromDto(it)) }
        } catch (e: Exception) {
            throw AppError.from(e)
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
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
            dao.updateLikeById(id)
            val response = apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            dao.updateLikeById(id)
            throw NetworkError
        } catch (e: Exception) {
            dao.updateLikeById(id)
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            val newPosts = body.toEntity().map { it.copy(isVisible = false) }
            newPosts.forEach { dao.insert(it) }
            emit(newPosts.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun makeAllPostsVisible() {
        dao.makeAllPostsVisible()
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


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