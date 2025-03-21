package ru.tatalaraydar.nmedia.viewmodel

import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    fun login(login: String, password: String) {
        //TODO авторизация
        println("Login: $login, Password: $password")
    }
}