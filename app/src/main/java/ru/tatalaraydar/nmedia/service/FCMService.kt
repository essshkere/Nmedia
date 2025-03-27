package ru.tatalaraydar.nmedia.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
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
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()


    override fun onCreate() {
        super.onCreate()
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
        // TODO: replace this in homework
        println(message.data["content"])
    }

    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
        println(token)
        Log.d("TOKEN", "Токен: $token")
    }



    private fun handleDefaultAction() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(R.string.notification_other)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)

        Log.d("FCM2", "Неизвестное действие получено")
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    private fun handlePost(content: Post) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_post,
                    content.userName
                )
            )
            .setStyle(NotificationCompat.BigTextStyle().bigText(content.contentPost))

            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }


    private fun notify(notification: Notification) {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }

    enum class Action {
        LIKE,
        POST,
    }

    data class Like(
        val userId: Long,
        val userName: String,
        val postId: Long,
        val postAuthor: String,
    )

    data class Post(
        val userId: Long,
        val userName: String,
        val postId: Long,
        val postAuthor: String,
        val contentPost: String
    )

}