package ru.tatalaraydar.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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
                callback.onError(Exception(t))
            }
        })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("Error saving post: ${response.message()}"))
                    return
                }
                val savedPost = response.body() ?: throw RuntimeException("Body is null")
                callback.onSuccess(savedPost)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception("Error saving post", t))
            }
        })
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    val deletedPost = response.body()
                    if (deletedPost != null) {
                        callback.onSuccess(deletedPost)
                    } else {
                        callback.onError(IOException("Body is null, successful"))
                    }
                } else {
                    callback.onError(IOException("Unexpected response code: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }


    override fun likeById(post: Post, callback: PostRepository.Callback<Post>) {
        val call = if (post.likedByMe) {
            PostsApi.retrofitService.dislikeById(post.id)
        } else {
            PostsApi.retrofitService.likeById(post.id)
        }

        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val likedPost = response.body() ?: throw RuntimeException("Body is null")
                    callback.onSuccess(likedPost)
                } else {
                    callback.onError(IOException("Unexpected response code: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception("Error like", t))
            }
        })
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