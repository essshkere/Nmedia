package ru.tatalaraydar.nmedia.repository


import android.annotation.SuppressLint
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
import java.util.concurrent.TimeUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.tatalaraydar.nmedia.error.ApiError
import ru.tatalaraydar.nmedia.dao.PostDao
import ru.tatalaraydar.nmedia.entity.PostEntity
import ru.tatalaraydar.nmedia.error.NetworkError
import androidx.lifecycle.*
import ru.tatalaraydar.nmedia.api.*
import ru.tatalaraydar.nmedia.entity.toDto
import ru.tatalaraydar.nmedia.entity.toEntity
import ru.tatalaraydar.nmedia.error.UnknownError
import okio.IOException
import ru.tatalaraydar.nmedia.entity.toEntity




class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll().map(List<PostEntity>::toDto)


    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts


    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

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

//    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
//        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
//            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
//                callback.onError(Exception(t))
//            }
//        })
//    }
//
//    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
//        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException("Error saving post: ${response.message()}"))
//                    return
//                }
//                val savedPost = response.body() ?: throw RuntimeException("Body is null")
//                callback.onSuccess(savedPost)
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//                callback.onError(Exception("Error saving post", t))
//            }
//        })
//    }

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

//    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
//        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
//            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
//                if (response.isSuccessful) {
//                    val deletedPost = response.body()
//                    if (deletedPost != null) {
//                        callback.onSuccess(deletedPost)
//                    } else {
//                        callback.onError(IOException("Body is null, successful"))
//                    }
//                } else {
//                    callback.onError(IOException("Unexpected response code: ${response.code()}"))
//                }
//            }
//
//            override fun onFailure(call: Call<Unit>, t: Throwable) {
//                callback.onError(Exception(t))
//            }
//        })
//    }

    override suspend fun likeById(id: Long) {
        TODO("Not yet implemented")
    }


//    override fun likeById(post: Post, callback: PostRepository.Callback<Post>) {
//        val call = if (post.likedByMe) {
//            PostsApi.retrofitService.dislikeById(post.id)
//        } else {
//            PostsApi.retrofitService.likeById(post.id)
//        }
//
//        call.enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (response.isSuccessful) {
//                    val likedPost = response.body() ?: throw RuntimeException("Body is null")
//                    callback.onSuccess(likedPost)
//                } else {
//                    callback.onError(IOException("Unexpected response code: ${response.code()}"))
//                }
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//                callback.onError(Exception("Error like", t))
//            }
//        })
//    }

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