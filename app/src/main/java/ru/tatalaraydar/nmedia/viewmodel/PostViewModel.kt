package ru.tatalaraydar.nmedia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.repository.PostRepository
import ru.tatalaraydar.nmedia.repository.PostRepositoryFileImpl
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "view"
    var postId: Long = 0L
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

//    fun findPostById(id: Long): Post? {
//        return data.value?.find { it.id == id }
//    }

//    fun findPostIdById(id: Long): Long? {
//        Log.d(TAG, "findPostIdById called with id: $id")
//        val postList = data.value
//        Log.d(TAG, "Current post list: $postList")
//        val post = postList?.find { it.id == id }
//
//        return if (post != null) {
//            Log.d(TAG, "Post found: $post")
//            post.id
//        } else {
//            Log.d(TAG, "No post found for id: $id")
//            null
//        }
//    }

    fun findPostIdById(id: Long): LiveData<Post?> {
        val result = MutableLiveData<Post?>()
        Log.d(TAG, "findPostById called with id: $id")
        val postList = data.value
        Log.d(TAG, "Current post list: $postList")
        val post = postList?.find { it.id == id }
        if (post != null) {
            Log.d(TAG, "Post found: $post")
            result.value = post
        } else {
            Log.d(TAG, "No post found for id: $id")
            result.value = null
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



