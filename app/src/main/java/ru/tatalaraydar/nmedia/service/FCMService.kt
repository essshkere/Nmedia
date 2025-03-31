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
import ru.tatalaraydar.nmedia.dto.*
import kotlin.random.Random



class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val recipientIdKey = "recipientId"
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
        try {
            val contentJson = message.data["content"] ?: run {
                Log.w("FCM", "Message content is null")
                return
            }


            val pushData = try {
                gson.fromJson(contentJson, PushData::class.java).apply {
                    Log.d("FCM", "Parsed push data: $this")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error parsing push data", e)
                return
            }

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
                    auth.sendPushToken()
                    Log.d("FCM", "Resending token for mismatched recipient")
                }
                else -> {
                    showNotification(pushData.content)
                }
            }
        } catch (e: Exception) {
            Log.e("FCM", "Error processing FCM message", e)
        }
    }

    private fun showNotification(content: NotificationContent) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(content.title ?: getString(R.string.notification_other))
            .setContentText(content.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (checkNotificationPermission()) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }




    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
        Log.d("FCM", "New token: $token")
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

    private fun checkNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    }
}