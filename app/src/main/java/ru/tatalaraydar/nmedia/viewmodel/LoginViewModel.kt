package ru.tatalaraydar.nmedia.viewmodel

import kotlinx.coroutines.launch
import retrofit2.Response
import ru.tatalaraydar.nmedia.api.*
import ru.tatalaraydar.nmedia.auth.AppAuth
import ru.tatalaraydar.nmedia.dto.AuthResponse
import ru.tatalaraydar.nmedia.error.*
import ru.tatalaraydar.nmedia.util.SingleLiveEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException

class LoginViewModel : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    private val _error = SingleLiveEvent<AppError>()
    val error: LiveData<AppError> get() = _error

    fun login(login: String, password: String) {
        _authState.value = AuthState.Loading


        viewModelScope.launch {
            try {

                val response: Response<AuthResponse> =
                    Api.service.authenticate(login, password)
                if (response.isSuccessful) {
                    val authResponse = response.body()!!
                    AppAuth.getInstance().setAuth(authResponse.id, authResponse.token)
                    _authState.value = AuthState.Success
                } else {

                    _error.value = ApiError(response.code(), "error_api")
                    _authState.value = AuthState.Error
                }
            } catch (e: IOException) {

                _error.value = NetworkError
                _authState.value = AuthState.Error
            } catch (e: Exception) {

                _error.value = UnknownError
                _authState.value = AuthState.Error
            }
        }
    }

    sealed class AuthState {
        object Success : AuthState()
        object Error : AuthState()
        object Loading : AuthState()
    }
}
