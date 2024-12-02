package ru.tatalaraydar.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.repository.PostRepository
import ru.tatalaraydar.nmedia.repository.PostRepositoryFileImpl




class PostViewModel (application: Application): AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryFileImpl(application)
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)

    private val empty = Post(
        id = 0,
        content = "",
        author = "",
        likedByMe = false,
        published = ""
    )

    fun findPostById(id: Long): Post? {
        return data.value?.find { it.id == id }
    }

    val edited = MutableLiveData(empty)

    fun like(id: Long) {
        repository.updateLikeById(id)
    }

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun startEditing(post: Post) {
        edited.value = post
    }

    fun сhangeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun remove(id: Long) = repository.removeById(id)
}



