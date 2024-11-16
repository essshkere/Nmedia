package ru.tatalaraydar.nmedia.dto

data class Post (
    val id: Long = 0,
    val author : String = "",
    val published: String = "",
    val authorAvatar :String = "",
    val content: String = "",
    var likes : Int = 0,
    var share : Int = 0,
    val views_post : Int = 0,
    var likedByMe: Boolean = false
)
