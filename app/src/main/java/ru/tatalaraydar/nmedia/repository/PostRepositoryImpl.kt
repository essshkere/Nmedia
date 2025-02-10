package ru.tatalaraydar.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.math.floor
import ru.tatalaraydar.nmedia.api.PostsApi
import ru.tatalaraydar.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostRepositoryRoomImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    init {
        _posts.value = getAll()
    }

    override fun getAll(): List<Post> {
        return try {
            val request: Request = Request.Builder()
                .url("${BASE_URL}/api/slow/posts")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw RuntimeException("body is null")
            gson.fromJson(responseBody, typeToken.type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        val json = gson.toJson(post)
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback.onSuccess(Unit)
                } else {
                    callback.onError(IOException("Unexpected response code: ${response.code}"))
                }
            }
        })
    }

    override fun removeById (id: Long, callback: PostRepository.Callback<Post>){
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .delete()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback.onSuccess(Unit)
                } else {
                    callback.onError(IOException("Unexpected response code: ${response.code}"))
                }
            }
        })
    }

    override fun likeById(post: Post, callback: PostRepository.Callback<Post>) {
        val method = if (post.likedByMe) "DELETE" else "POST"
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/${post.id}/likes")
            .method(method, "".toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: throw RuntimeException("body is null")
                if (response.isSuccessful) {
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                } else {
                    callback.onError(IOException("Unexpected response code: ${response.code}"))
                }}})

    }

    private fun getPostById(id: Long): Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()
        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, Post::class.java)
            }
    }

    override fun updateShareById(id: Long) {
        val request: Request = Request.Builder()
            .post("{}".toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts/$id/shares")
            .build()
        client.newCall(request)
            .execute()
            .close()
        val currentPosts = _posts.value ?: emptyList()
        _posts.postValue(currentPosts.map {
            if (it.id == id) it.copy(share = it.share + 1) else it
        })
    }

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