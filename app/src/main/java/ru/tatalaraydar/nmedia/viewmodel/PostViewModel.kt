package ru.tatalaraydar.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.tatalaraydar.nmedia.auth.AppAuth
import ru.tatalaraydar.nmedia.dto.FeedItem
import ru.tatalaraydar.nmedia.dto.MediaUpload
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.model.FeedModel
import ru.tatalaraydar.nmedia.model.FeedModelState
import ru.tatalaraydar.nmedia.model.PhotoModel
import ru.tatalaraydar.nmedia.repository.PostRepositoryImpl
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepositoryImpl, auth: AppAuth
) : ViewModel() {

    val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = { repository.getPagingSource() }
    ).flow.cachedIn(viewModelScope)

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedItem>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    if (post is Post) {
                        post.copy(ownedByMe = post.authorId == myId)
                    } else {
                        post
                    }
                }
            }
        }

    private val _data = MutableStateFlow(FeedModel())


    private val _dataState = MutableStateFlow(FeedModelState())
    val dataState: StateFlow<FeedModelState> = _dataState.asStateFlow()

    private val _edited = MutableStateFlow(emptyPost())
    val edited: StateFlow<Post> = _edited.asStateFlow()

    private val _photo = MutableStateFlow(PhotoModel())
    val photo: StateFlow<PhotoModel> = _photo.asStateFlow()



    init {
        loadPosts()
        auth.authStateFlow
            .onEach { refreshAll() }
            .launchIn(viewModelScope)
    }

    private fun refreshAll() {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
                _dataState.value = _dataState.value.copy(error = true)
            }
        }
    }

    fun loadPosts() = viewModelScope.launch {
        _dataState.value = _dataState.value.copy(loading = true)
        try {

            _dataState.value = _dataState.value.copy(loading = false, error = false)
        } catch (e: Exception) {
            _dataState.value = _dataState.value.copy(loading = false, error = true)
        }
    }

    fun save() {
        edited.value.let { post ->
            viewModelScope.launch {
                try {
                    if (photo.value.uri != null) {
                        repository.saveWithAttachment(post, MediaUpload(photo.value.file!!))
                    } else {
                        repository.save(post, photo.value.file?.let { MediaUpload(it) })
                    }
                    _edited.value = emptyPost()
                    _photo.value = PhotoModel()
                } catch (e: Exception) {
                    _dataState.value = _dataState.value.copy(error = true)
                }
            }
        }
    }

    fun changeContent(content: String) {
        _edited.value = _edited.value.copy(content = content)
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun share(post:Post){}

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
        } catch (e: Exception) {
            _dataState.value = _dataState.value.copy(error = true)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _dataState.value = _dataState.value.copy(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        _dataState.value = _dataState.value.copy(refreshing = true)
        try {
            _dataState.value = _dataState.value.copy(refreshing = false, error = false)
        } catch (e: Exception) {
            _dataState.value = _dataState.value.copy(refreshing = false, error = true)
        }
    }

    private fun emptyPost() = Post(
        id = 0,
        authorId = 0,
        author = "",
        authorAvatar = "",
        content = "",
        published = 0,
        likedByMe = false,
        likes = 0,
        attachment =null,
        ownedByMe = false
    )
}