package ru.tatalaraydar.nmedia.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.tatalaraydar.nmedia.dto.*


interface ApiService {
    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>


    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>


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

    @GET("posts")
    suspend fun getPosts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<Post>>

    @GET("posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
}

