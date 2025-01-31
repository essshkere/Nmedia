package ru.tatalaraydar.nmedia.model

import ru.tatalaraydar.nmedia.dto.Post


data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false,
    val refreshing: Boolean = false,
)