package ru.tatalaraydar.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.tatalaraydar.nmedia.api.ApiService
import ru.tatalaraydar.nmedia.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val apiService: ApiService
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _data = MutableStateFlow(appAuth.authStateFlow.value)
    val data: StateFlow<AppAuth.AuthState> = _data.asStateFlow()

    val authenticated: Boolean
        get() = _data.value.id != 0L

    init {
        viewModelScope.launch {
            appAuth.authStateFlow.collect { authState ->
                _data.value = authState
            }
        }
    }

    fun login(login: String, password: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val response = apiService.authenticate(login, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        appAuth.setAuth(it.id, it.token)
                        _authState.value = AuthState.Success
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            appAuth.removeAuth()
        }
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        class Error(val error: Throwable) : AuthState()
    }
}