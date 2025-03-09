package ru.tatalaraydar.nmedia.repository


import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import kotlin.math.floor
import ru.tatalaraydar.nmedia.api.PostsApi
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.error.ApiError
import ru.tatalaraydar.nmedia.dao.PostDao
import ru.tatalaraydar.nmedia.entity.PostEntity
import ru.tatalaraydar.nmedia.error.NetworkError
import androidx.lifecycle.*
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import ru.tatalaraydar.nmedia.api.*
import ru.tatalaraydar.nmedia.entity.toDto
import ru.tatalaraydar.nmedia.entity.toEntity
import ru.tatalaraydar.nmedia.error.UnknownError
import okio.IOException
import ru.tatalaraydar.nmedia.db.AppDb
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.tatalaraydar.nmedia.error.AppError


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            // dao.insert(body.toEntity())
            body.toEntity().forEach { dao.insert(it) }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = PostsApi.service.getNewer(id)
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


    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
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

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
    override suspend fun makeAllPostsVisible() {
        dao.makeAllPostsVisible()
    }

    override suspend fun likeById(id: Long) {

        try {
            dao.updateLikeById(id)
            val response = PostsApi.service.likeById(id)
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


    private fun buildDatabase(context: Context) =

        Room.databaseBuilder(context, AppDb::class.java, "app.db")

            .fallbackToDestructiveMigration()


            .build()

    override fun updateShareById(id: Long) {
//        val request: Request = Request.Builder()
//            .post("{}".toRequestBody(jsonType))
//            .url("${BASE_URL}/api/slow/posts/$id/shares")
//            .build()
//        client.newCall(request)
//            .execute()
//            .close()
//        val currentPosts = _posts.value ?: emptyList()
//        _posts.postValue(currentPosts.map {
//            if (it.id == id) it.copy(share = it.share + 1) else it
//        })
    }

    companion object {
        private const val KEY = "id"
        private const val ID = "posts"
        private const val FILENAME = "posts.json"
        private val gson = Gson()
        private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type

        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()

        @SuppressLint("DefaultLocale")
        fun formatCount(count: Int): String {
            return when {
                count >= 1_000_000 -> String.format("%.1fM", floor(count / 1_000_000.0 * 10) / 10)
                    .replace(",", ".")

                count >= 1_000 -> String.format("%.1fK", floor(count / 1_000.0 * 10) / 10)
                    .replace(",", ".")

                else -> count.toString()
            }
        }
    }
}