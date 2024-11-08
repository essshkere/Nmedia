package ru.tatalaraydar.nmedia

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.tatalaraydar.nmedia.databinding.ActivityMainBinding
import ru.tatalaraydar.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        val post = Post(
            1, "Нетология. Университет интернет-профессий будущего", "net", "10 мая",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению.",
            10, 5, 10, false
        )
        binding.content.text = post.content
        binding.published.text = post.published
        binding.author.text = post.author
        updatelike(binding,post)
        binding.buttonLikes.setOnClickListener {
            post.likedByMe = !post.likedByMe
            updatelike(binding,post)
        }
    }

    private fun updatelike(binding: ActivityMainBinding, post: Post) {
        binding.buttonLikes.setImageResource(
            if (post.likedByMe) {
                R.drawable.baseline_thumb_up_red
            } else {
                R.drawable.baseline_thumb_up_alt_24
            }
        )
    }
}

