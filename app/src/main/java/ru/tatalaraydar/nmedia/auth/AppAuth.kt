package ru.tatalaraydar.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.tatalaraydar.nmedia.api.ApiService
import ru.tatalaraydar.nmedia.dto.PushToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    private val _authStateFlow: MutableStateFlow<AuthState> = MutableStateFlow(
        AuthState(
            id = prefs.getLong(idKey, 0),
            token = prefs.getString(tokenKey, null)
        ).takeIf { it.id != 0L && it.token != null } ?: AuthState()
    )

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()
    val authenticated: Boolean get() = _authStateFlow.value.id != 0L

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        prefs.edit {
            putLong(idKey, id)
            putString(tokenKey, token)
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        prefs.edit { clear() }
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                apiService.save(
                    PushToken(
                        token = token ?: Firebase.messaging.token.await(),
                        recipientId = _authStateFlow.value.id.takeIf { it != 0L }
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    data class AuthState(val id: Long = 0, val token: String? = null)
}