package ru.tatalaraydar.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.tatalaraydar.nmedia.repository.PostRepository
import ru.tatalaraydar.nmedia.repository.PostRepositoryInMemory

class PostViewModel : ViewModel() {
    private val repository: PostRepositoryInMemory = PostRepositoryInMemory()
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)

    fun like(id: Long) {
        repository.updateLikeById(id)
    }

    fun share(id: Long) {
        repository.updateShareById(id)
    }
}


