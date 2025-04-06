package ru.tatalaraydar.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.tatalaraydar.nmedia.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {

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
}