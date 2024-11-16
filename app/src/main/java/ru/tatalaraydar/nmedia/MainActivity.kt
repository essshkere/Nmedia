package ru.tatalaraydar.nmedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tatalaraydar.nmedia.databinding.ActivityMainBinding

import ru.tatalaraydar.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels
import ru.tatalaraydar.nmedia.databinding.CardPostBinding
import ru.tatalaraydar.nmedia.repository.PostRepositoryInMemory
import ru.tatalaraydar.nmedia.adapter.PostsAdapter


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel:PostViewModel by viewModels()
        val adapter = PostsAdapter(
            onLikeListener = { post ->
                viewModel.like(post.id)  },
            onShareListener = { post ->
                viewModel.share(post.id)   }
        )
        binding.container.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)


        }
    }
}





