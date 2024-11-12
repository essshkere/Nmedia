package ru.tatalaraydar.nmedia.repository

import androidx.lifecycle.LiveData
import ru.tatalaraydar.nmedia.dto.Post

interface PostRepository {
    fun getPost(): LiveData<Post>
    fun like()

}