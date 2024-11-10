package ru.tatalaraydar.nmedia.dto

data class Post (
    val id: Long = 0,
    val author : String = "",
    val authorAvatar : String = "",
    val published: String = "",
    val content: String = "",
    var likes : Int = 0,
    var share : Int = 0,
    var views_post : Int = 0,
    var likedByMe: Boolean = false,
)
