package ru.tatalaraydar.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.OkHttpClient
import ru.tatalaraydar.nmedia.db.AppDb
import ru.tatalaraydar.nmedia.repository.*
import ru.tatalaraydar.nmedia.util.SingleLiveEvent
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*
import ru.tatalaraydar.nmedia.auth.AppAuth
import ru.tatalaraydar.nmedia.dto.*
import ru.tatalaraydar.nmedia.model.*
import java.io.File

private val empty = Post(
    id = 0,
    content = "",
    authorId = 0,
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = 0.toString(),
)
private val noPhoto = PhotoModel()

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val gson = Gson()
    var postId: Long = 0L
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    val data: LiveData<FeedModel> = AppAuth.getInstance()
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.authorId == myId) },
                        posts.isEmpty()
                    )
                }
        }.asLiveData(Dispatchers.Default)

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
        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    if (_photo.value?.uri != null && _photo.value?.file != null) {
                        // Если есть вложение, используем saveWithAttachment
                        repository.saveWithAttachment(post, MediaUpload(_photo.value!!.file!!))
                    } else {
                        // Если вложения нет, сохраняем обычный пост
                        repository.save(post)
                    }
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