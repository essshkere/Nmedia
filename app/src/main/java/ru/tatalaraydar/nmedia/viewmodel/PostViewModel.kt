package ru.tatalaraydar.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.tatalaraydar.nmedia.repository.PostRepositoryInMemory

class PostViewModel : ViewModel(){
    private val repository = PostRepositoryInMemory()
    val post = repository.getPost()


    fun like () {
        repository.updateLike()
    }

    fun share () {
        repository.updateShare()
    }
}