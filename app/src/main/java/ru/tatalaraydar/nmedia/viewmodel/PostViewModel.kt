package ru.tatalaraydar.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import okhttp3.OkHttpClient
import ru.tatalaraydar.nmedia.db.AppDb
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.model.FeedModel
import ru.tatalaraydar.nmedia.repository.PostRepository
import ru.tatalaraydar.nmedia.repository.PostRepositoryImpl
import ru.tatalaraydar.nmedia.util.SingleLiveEvent
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tatalaraydar.nmedia.model.FeedModelState
import kotlinx.coroutines.flow.*
import ru.tatalaraydar.nmedia.model.PhotoModel
import java.io.File

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = ""
)
private val noPhoto = PhotoModel()
class PostViewModel(application: Application) : AndroidViewModel(application) {



    private val gson = Gson()
    var postId: Long = 0L
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    val data: LiveData<FeedModel> = repository.data
        .map(::FeedModel)
        .asLiveData(Dispatchers.Default)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    private val _data = MutableLiveData(FeedModel())
    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    fun findPostIdById(id: Long): LiveData<Post?> {
        val result = MediatorLiveData<Post?>()
        return result
    }

    private val client = OkHttpClient()

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }
    fun removePhoto() {
        _photo.value = null
    }


    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun makeAllPostsVisible() {
        viewModelScope.launch {
            repository.makeAllPostsVisible()
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }



    fun edit(post: Post) {
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



