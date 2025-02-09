package ru.tatalaraydar.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson

import okhttp3.OkHttpClient
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.model.FeedModel
import ru.tatalaraydar.nmedia.repository.PostRepository
import ru.tatalaraydar.nmedia.repository.PostRepositoryRoomImpl
import ru.tatalaraydar.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val gson = Gson()
    var postId: Long = 0L
    private val repository: PostRepository = PostRepositoryRoomImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data

    val edited = MutableLiveData(empty)

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _postCreated = SingleLiveEvent<Unit>()

    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun findPostIdById(id: Long): LiveData<Post?> {
        val result = MediatorLiveData<Post?>()
        return result
    }

    private val client = OkHttpClient()

    fun save() {
        edited.value?.let { post ->
            repository.save(post, object : PostRepository.CustomCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    _postCreated.postValue(Unit)
                }
                override fun onError(error: Throwable) {
                    _error.postValue(error)
                }
            })
        }
        edited.value = empty
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.value = _data.value?.copy(posts = _data.value?.posts.orEmpty().filter { it.id != id })
        repository.removeById(id, object : PostRepository.CustomCallback<Unit> {
            override fun onSuccess(result: Unit) {
            }
            override fun onError(error: Throwable) {
                _data.postValue(_data.value?.copy(posts = old))
            }})}

    fun likeById(post: Post) {
        repository.likeById(post, object : PostRepository.CustomCallback<Post> {
            override fun onSuccess(updatedPost: Post) {
                val currentPosts = _data.value?.posts ?: emptyList()
                _data.postValue(
                    _data.value?.copy(
                        posts = currentPosts.map {
                            if (it.id == updatedPost.id) updatedPost else it
                        }))}
            override fun onError(error: Throwable) {
                error.printStackTrace()
            }})}

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun startEditing(post: Post) {
        edited.value = post
    }

    fun updatePost(postId: Long, updatedContent: String) {
//        val postToUpdate = data.value?.find { it.id == postId }
//        if (postToUpdate != null) {
//            val updatedPost = postToUpdate.copy(content = updatedContent)
//            repository.save(updatedPost)
//        }
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun share(id: Long) {
        repository.updateShareById(id)
    }
}



