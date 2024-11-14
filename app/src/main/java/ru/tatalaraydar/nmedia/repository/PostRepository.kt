package ru.tatalaraydar.nmedia.repository

import androidx.lifecycle.LiveData
import ru.tatalaraydar.nmedia.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
}