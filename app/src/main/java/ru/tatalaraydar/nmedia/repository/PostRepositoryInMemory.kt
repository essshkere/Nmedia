package ru.tatalaraydar.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tatalaraydar.nmedia.R
import ru.tatalaraydar.nmedia.databinding.ActivityMainBinding
import ru.tatalaraydar.nmedia.dto.Post
import kotlin.math.floor

class PostRepositoryInMemory : PostRepository {
    private val data = MutableLiveData(
        Post(
            1,
            "Нетология. Университет интернет-профессий будущего",
            "net",
            "10 мая в 19:45",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            11111,
            999_999,
            1000_000,
            false
        )

    )

    override fun getPost(): LiveData<Post> = data

    override fun like() {
        val currentPost = data.value?: return
        val updatedPost = currentPost?.copy(
            likedByMe = currentPost.likedByMe,
            likes = if (currentPost.likedByMe){0} else {1}
        )
        data.value = updatedPost
    }
    fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fM", floor(count / 1_000_000.0 * 10) / 10).replace(",", ".")
            count >= 1_000 -> String.format("%.1fK", floor(count / 1_000.0 * 10) / 10).replace(",", ".")
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
    fun updateShare(binding: ActivityMainBinding, post: Post) {
        post.share++
        binding.share.text = formatCount(post.share)
    }

    companion object


}