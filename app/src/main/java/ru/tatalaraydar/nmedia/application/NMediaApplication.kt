package ru.tatalaraydar.nmedia.application

import android.app.Application
import ru.tatalaraydar.nmedia.auth.AppAuth

class NMediaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
}