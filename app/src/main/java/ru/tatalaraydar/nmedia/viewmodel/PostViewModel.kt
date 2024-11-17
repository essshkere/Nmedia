package ru.tatalaraydar.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.repository.PostRepositoryInMemory

class PostViewModel : ViewModel() {
    private val repository: PostRepositoryInMemory = PostRepositoryInMemory()
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)

    private val empty = Post(
        id = 0,
        content = "",
        author = "",
        likedByMe = false,
        published = ""
    )

    val edited = MutableLiveData(empty)

    fun like(id: Long) {
        repository.updateLikeById(id)
    }

    fun share(id: Long) {
        repository.updateShareById(id)
    }

    fun save() {

        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {

        edited.value = post

    }

    fun cancelEdit() {
        edited.value = empty


    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun remove(id: Long) = repository.removeById(id)

    fun isPostBeingEdited(postId: Long): Boolean {
        return edited.value?.id == postId


    }
}

