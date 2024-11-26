package ru.tatalaraydar.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.adapter.OnInteractionListener
import ru.tatalaraydar.nmedia.adapter.PostsAdapter
import ru.tatalaraydar.nmedia.databinding.ActivityMainBinding
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newPostLauncher = registerForActivityResult(NewPostActivity.NewPostContract) { result ->
            result ?: return@registerForActivityResult
            viewModel.сhangeContent(result)
            viewModel.save()
        }

        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onRemove(post: Post) {
                viewModel.remove(post.id)
            }

            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }



            override fun onEdit(post: Post) {
                val intent = Intent(this@MainActivity, EditPostActivity::class.java).apply {
                    putExtra("post_id", post.id)
                    putExtra("post_content", post.content)
                }
                startActivityForResult(intent, EDIT_POST_REQUEST_CODE)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onVideolink(post:Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoURL))
                startActivity(intent)
            }

        })

        binding.container.adapter = adapter

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        binding.save.setOnClickListener {
            newPostLauncher.launch(Unit)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_POST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val updatedContent = data?.getStringExtra("updated_content") ?: return
            val postId = data.getLongExtra("post_id", 0L)

            val postToEdit = viewModel.findPostById(postId)
            if (postToEdit != null) {
                viewModel.startEditing(postToEdit)
                viewModel.сhangeContent(updatedContent)
                viewModel.save()
            }
        }
    }
    companion object {
        private const val EDIT_POST_REQUEST_CODE = 100
    }
}

