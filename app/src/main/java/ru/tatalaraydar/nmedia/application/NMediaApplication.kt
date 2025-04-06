package ru.tatalaraydar.nmedia.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.tatalaraydar.nmedia.auth.AppAuth
import javax.inject.Inject

@HiltAndroidApp
class NMediaApplication : Application() {
    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
    }
}