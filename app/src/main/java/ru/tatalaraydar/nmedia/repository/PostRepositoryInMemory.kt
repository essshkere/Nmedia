package ru.tatalaraydar.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tatalaraydar.nmedia.dto.Post

class PostRepositoryInMemory : PostRepository {
    private val data = MutableLiveData(
        Post(
            1,
            "Нетология. Университет интернет-профессий будущего",
            "net",
            "10 мая в 19:45",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            1000,
            999_999,
            1100_000,
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
}