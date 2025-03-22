package ru.tatalaraydar.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.activity.NewPostFragment.Companion.textArg
import ru.tatalaraydar.nmedia.auth.AppAuth
import ru.tatalaraydar.nmedia.databinding.ActivityAppBinding
import ru.tatalaraydar.nmedia.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationsPermission()

        val viewModel: AuthViewModel by viewModels()

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, R.string.error_empty_content, LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
            } else {
                if (text.contains("edit")) {
                    findNavController(R.id.fragment_container).navigate(
                        R.id.action_feedFragment_to_editPostFragment,
                        Bundle().apply { textArg = text }
                    )
                } else {
                    findNavController(R.id.fragment_container).navigate(
                        R.id.action_feedFragment_to_newPostFragment,
                        Bundle().apply { textArg = text }
                    )
                }
            }
        }

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
                    it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.signin -> {
                        findNavController(R.id.fragment_container).navigate(R.id.action_feedFragment_to_loginFragment)
                        true
                    }

                    R.id.signup -> {
                        // TODO:  переход к фрагменту регистрации
                        true
                    }

                    R.id.signout -> {
                        // TODO Выход из аккаунта
                        AppAuth.getInstance().removeAuth()
                        true
                    }

                    else -> false
                }
        })
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }
}
