package ru.tatalaraydar.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.tatalaraydar.nmedia.api.ApiService
import ru.tatalaraydar.nmedia.auth.AppAuth
import ru.tatalaraydar.nmedia.error.ApiError
import ru.tatalaraydar.nmedia.error.NetworkError
import ru.tatalaraydar.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val appAuth: AppAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    fun login(login: String, password: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val response = apiService.authenticate(login, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        appAuth.setAuth(it.id, it.token)
                        _authState.value = AuthState.Success
                    } ?: run {
                        _authState.value = AuthState.Error(ApiError(response.code(), "Empty response body"))
                    }
                } else {
                    _authState.value = AuthState.Error(ApiError(response.code(), response.message()))
                }
            } catch (e: IOException) {
                _authState.value = AuthState.Error(NetworkError)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(UnknownError)
            }
        }
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        class Error(val error: Throwable) : AuthState()
    }
}