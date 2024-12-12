package ru.tatalaraydar.nmedia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.repository.PostRepository
import ru.tatalaraydar.nmedia.repository.PostRepositoryFileImpl
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dao.PostDaoImpl
import ru.tatalaraydar.nmedia.repository.PostRepositorySQLiteImpl


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "view"
    var postId: Long = 0L


    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )

        val data = repository . getAll ()




    fun findPostIdById(id: Long): LiveData<Post?> {
        val result = MediatorLiveData<Post?>()
        result.addSource(data) { posts ->
            Log.d(TAG, "findPostById called with id: $id")
            result.value = posts.find { it.id == id }
            Log.d(TAG, "Post found: $result.value")
        }
        return result
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

    fun updatePost(postId: Long, updatedContent: String) {
        val postToUpdate = data.value?.find { it.id == postId }
        if (postToUpdate != null) {
            val updatedPost = postToUpdate.copy(content = updatedContent)
            repository.save(updatedPost)
        }
    }

    fun changeContent(updatedContent: String) {
        val text = updatedContent.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun remove(id: Long) = repository.removeById(id)
}



