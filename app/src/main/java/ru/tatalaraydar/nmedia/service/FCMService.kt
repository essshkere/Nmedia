package ru.tatalaraydar.nmedia.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.auth.AppAuth
import kotlin.random.Random


class FCMService : FirebaseMessagingService() {
    private val channelId = "remote"
    private val gson = Gson()


    data class PushData(
        val recipientId: Long?,
        val content: String
    )

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        try {
            val contentJson = message.data["content"] ?: run {
                Log.w("FCM", "Message content is null")
                return
            }

            Log.d("FCM", "Received JSON: $contentJson")

            val pushData = try {
                gson.fromJson(contentJson, PushData::class.java).also {
                    Log.d("FCM", "Parsed push data: $it")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error parsing push data", e)
                return
            }

            handlePushData(pushData)
        } catch (e: Exception) {
            Log.e("FCM", "Error processing FCM message", e)
        }
    }

    private fun handlePushData(pushData: PushData) {
        val recipientId = pushData.recipientId
        val auth = AppAuth.getInstance()
        val myId = auth.authStateFlow.value.id

        when {
            recipientId == null -> {
                showNotification(pushData.content)
            }
            recipientId == 0L && myId != 0L -> {
                auth.sendPushToken()
                Log.d("FCM", "Resending token for anonymous auth")
            }
            recipientId != myId -> {
                Log.d("FCM", "Ignoring notification for another user")
            }
            else -> {
                showNotification(pushData.content)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(content: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_other))
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (checkNotificationPermission()) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "New token: $token")
        AppAuth.getInstance().sendPushToken(token)
    }

    private fun checkNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    }
}