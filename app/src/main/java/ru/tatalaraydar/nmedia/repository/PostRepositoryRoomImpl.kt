package ru.tatalaraydar.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import ru.tatalaraydar.nmedia.dao.PostDao
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.entity.PostEntity
import kotlin.math.floor
import java.util.concurrent.TimeUnit
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostRepositoryRoomImpl: PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun likeById(id: Long) {
        val request: Request = Request.Builder()
            .post(RequestBody.create(jsonType, "{}"))
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun updateShareById(id: Long) {
        val request: Request = Request.Builder()
            .post(RequestBody.create(jsonType, "{}"))
            .url("${BASE_URL}/api/slow/posts/$id/shares")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

//    override fun save(post: Post) {
//        dao.save(PostEntity.fromDto(post))
//    }
//
//    override fun removeById(id: Long) {
//        dao.removeById(id)
//    }
//
//    override fun likeById(id: Long) {
//        dao.updateLikeById(id)
//    }
//
//    override fun updateShareById(id: Long) {
//        dao.updateShareById(id)
//    }

    companion object {
        private const val KEY = "id"
        private const val ID = "posts"
        private const val FILENAME = "posts.json"
        private val gson = Gson()
        private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type

        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()

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