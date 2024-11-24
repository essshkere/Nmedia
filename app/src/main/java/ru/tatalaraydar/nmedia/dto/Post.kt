package ru.tatalaraydar.nmedia.dto

data class Post (
    val id: Long = 0,
    val author : String = "",
    val published: String = "",
    val authorAvatar :String = "",
    val content: String = "",
    var likes : Int = 999,
    var share : Int = 11099,
    val views_post : Int = 1_000_000,
    var likedByMe: Boolean = false,
    val videoURL: String = ""
)
