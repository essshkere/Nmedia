package ru.tatalaraydar.nmedia.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("FCM", token)
        println(token)
    }
    override fun onMessageReceived(message: RemoteMessage) {

//        message.data[action]?.let {
//            when (Action.valueOf(it)) {
//                Action.LIKE -> handleLike(gson.fromJson(message.data[content], Like::class.java))
//            }
//        }
    }
}