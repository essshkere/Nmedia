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
//        enableEdgeToEdge()
        setContentView(binding.root)
        val post = Post(
            1, "Нетология. Университет интернет-профессий будущего", "net", "10 мая в 19:45",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            1000, 999_999, 1100_000, false
        )

        binding.content.text = post.content
        binding.published.text = post.published
        binding.author.text = post.author
        binding.likes.text = formatCount(post.likes)
        binding.viewsPost.text = formatCount(post.views_post)
        binding.share.text = formatCount(post.share)

        updatelike(binding, post)

        binding.buttonShare.setOnClickListener { updateShare(binding, post) }
        binding.buttonLikes.setOnClickListener {
            post.likedByMe = !post.likedByMe
            updatelike(binding, post)
        }
    }

    private fun updatelike(binding: ActivityMainBinding, post: Post) {
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

    fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0).replace(",", ".")
            count >= 1_000 -> "${count / 1_000}K"
            else -> count.toString()
        }
    }

}

