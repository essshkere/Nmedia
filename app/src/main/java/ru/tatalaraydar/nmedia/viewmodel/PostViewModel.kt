package ru.tatalaraydar.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.tatalaraydar.nmedia.db.AppDb
import ru.tatalaraydar.nmedia.dto.MediaUpload
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.model.FeedModel
import ru.tatalaraydar.nmedia.model.FeedModelState
import ru.tatalaraydar.nmedia.model.PhotoModel
import ru.tatalaraydar.nmedia.repository.PostRepository
import ru.tatalaraydar.nmedia.repository.PostRepositoryImpl
import ru.tatalaraydar.nmedia.util.SingleLiveEvent
import java.io.File
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.*


class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> = repository.data
        .map { posts -> FeedModel(posts, posts.isEmpty()) }
        .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState> get() = _dataState

    private val _edited = MutableLiveData(emptyPost())
    val edited: LiveData<Post> get() = _edited

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> get() = _postCreated

    private val _photo = MutableLiveData<PhotoModel>()
    val photo: LiveData<PhotoModel> get() = _photo

    init {
        loadPosts()
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

    fun save() {
        edited.value?.let { post ->
            viewModelScope.launch {
                try {
                    if (_photo.value?.uri != null && _photo.value?.file != null) {
                        repository.saveWithAttachment(post, MediaUpload(_photo.value!!.file!!))
                    } else {
                        repository.save(post)
                    }
                    _postCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        _edited.value = emptyPost()
    }

    fun edit(postId: Long, content: String) {
        _edited.value = _edited.value?.copy(id = postId, content = content)
        viewModelScope.launch {
            try {
                repository.save(_edited.value ?: return@launch)
                _postCreated.value = Unit
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }

    fun updatePost(postId: Long, updatedContent: String) {

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

    fun changeContent(content: String) {
        val text = content.trim()
        if (_edited.value?.content == text) return
        _edited.value = _edited.value?.copy(content = text)
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun findPostIdById(id: Long): LiveData<Post?> {
        val result = MediatorLiveData<Post?>()
        return result
    }

    fun share(id: Long) {
        repository.updateShareById(id)
    }

    private fun emptyPost() = Post(
        id = 0,
        content = "",
        author = "",
        authorAvatar = "",
        published = "",
        likedByMe = false,
        likes = 0,
        share = 0,
        views_post = 0
    )

    companion object {
        const val NEW_POST_ID = 0L
    }



}