package ru.tatalaraydar.nmedia


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tatalaraydar.nmedia.databinding.ActivityMainBinding
import ru.tatalaraydar.nmedia.dto.Post
import ru.tatalaraydar.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()

        viewModel.post.observe(this) { post ->
            with(binding) {
                binding.content.text = post.content
                binding.published.text = post.published
                binding.author.text = post.author
                binding.likes.text = formatCount(post.likes)
                binding.viewsPost.text = formatCount(post.views_post)
                binding.share.text = formatCount(post.share)

                updatelike(binding, post)
                binding.root.setOnClickListener {

                }
                binding.buttonShare.setOnClickListener {
                    updateShare(binding, post)
                }
                binding.avatar.setOnClickListener {

                }

                binding.buttonLikes.setOnClickListener {
                    post.likedByMe = !post.likedByMe
                    updatelike(binding, post)
                }

            }






        }

    }

    fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
                .replace(",", ".")

            count >= 1_000 -> "${count / 1_000}K"
            else -> count.toString()
        }
    }
    fun updatelike(binding: ActivityMainBinding, post: Post) {
        binding.buttonLikes.setImageResource(
            if (post.likedByMe) {
                post.likes += 1
                binding.likes.text = formatCount(post.likes)
                R.drawable.baseline_thumb_up_red
            } else {
                post.likes -= 1
                binding.likes.text = formatCount(post.likes)
                R.drawable.baseline_thumb_up_alt_24
            }
        )
    }
    private fun updateShare(binding: ActivityMainBinding, post: Post) {
        post.share++
        binding.share.text = formatCount(post.share)
    }

}






