package ru.tatalaraydar.nmedia.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.tatalaraydar.nmedia.dto.*


interface ApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>


    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun authenticate(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthResponse>
}

