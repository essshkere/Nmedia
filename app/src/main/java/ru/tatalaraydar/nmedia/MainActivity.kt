package ru.tatalaraydar.nmedia

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.tatalaraydar.nmedia.databinding.ActivityMainBinding
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels
import androidx.constraintlayout.widget.Group
import ru.tatalaraydar.nmedia.adapter.OnInteractionListener
import ru.tatalaraydar.nmedia.adapter.PostsAdapter
import ru.tatalaraydar.nmedia.dto.Post


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onShare(post: Post) {
                viewModel.share(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.remove(post.id)
            }

            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }
        })
        binding.container.adapter = adapter
        viewModel.data.observe(this)
        { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                return@observe
            }
            with(binding.content) {
                requestFocus()
                setText(post.content)
            }
        }
        viewModel.isEditing.observe(this) { isEditing ->
            val visibility = if (isEditing) View.VISIBLE else View.GONE
            findViewById<Group>(R.id.group).visibility = visibility

        }

        binding.cancel.setOnClickListener {
            viewModel.cancelEdit()
        }


        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()

                setText("")
                clearFocus()

            }
        }
    }
}







