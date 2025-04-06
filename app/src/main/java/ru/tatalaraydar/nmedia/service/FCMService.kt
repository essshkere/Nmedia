package ru.tatalaraydar.nmedia.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.tatalaraydar.nmedia.auth.AppAuth
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject lateinit var appAuth: AppAuth

    private val gson = Gson()
    private val channelId = "remote"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        try {
            val content = message.data["content"] ?: run {
                Log.w("FCM", "Empty message content")
                return
            }

            val pushData = try {
                gson.fromJson(content, PushData::class.java)
            } catch (e: Exception) {
                Log.e("FCM", "Error parsing push data", e)
                return
            }

            handlePushData(pushData)
        } catch (e: Exception) {
            Log.e("FCM", "Message handling error", e)
        }
    }

    override fun onNewToken(token: String) {
        appAuth.sendPushToken(token)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.channel_remote_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_remote_description)
            }

            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun handlePushData(pushData: PushData) {
        when {
            pushData.recipientId == null -> showNotification(pushData.content)
            pushData.recipientId == 0L && appAuth.authStateFlow.value.id != 0L ->
                appAuth.sendPushToken()
            pushData.recipientId != appAuth.authStateFlow.value.id ->
                Log.d("FCM", "Ignoring notification for another user")
            else -> showNotification(pushData.content)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(content: String) {
        if (!hasNotificationPermission()) return

        NotificationManagerCompat.from(this).notify(
            System.currentTimeMillis().toInt(),
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.notification_other))
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        )
    }

    private fun hasNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    }

    private data class PushData(
        val recipientId: Long?,
        val content: String
    )
}