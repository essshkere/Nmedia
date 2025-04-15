package ru.tatalaraydar.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.tatalaraydar.nmedia.auth.AppAuth
import ru.tatalaraydar.nmedia.util.StringArg
import ru.tatalaraydar.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    var Bundle.textArg: String? by StringArg
    private lateinit var binding: ActivityAppBinding

    @Inject lateinit var appAuth: AppAuth
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setSupportActionBar(binding.topAppBar)
        requestNotificationsPermission()
        setupAuthMenu()
        handleIntent()
    }

    private fun setupAuthMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                menu.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
                menu.setGroupVisible(R.id.authenticated, viewModel.authenticated)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
                R.id.signin -> {
                    findNavController(R.id.fragment_container).navigate(R.id.action_feedFragment_to_loginFragment)
                    true
                }
                R.id.signup -> true
                R.id.signout -> {
                    appAuth.removeAuth()
                    true
                }
                else -> false
            }
        })

        viewModel.data.flowWithLifecycle(lifecycle)

            .onEach { invalidateOptionsMenu() }

            .launchIn(lifecycleScope)
    }

    private fun handleIntent() {
        intent?.let {
            if (it.action != Intent.ACTION_SEND) return@let

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, R.string.error_empty_content, LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) { finish() }
                    .show()
            } else {
                findNavController(R.id.fragment_container).navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply { textArg = text }
                )
            }
        }
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
    }
}