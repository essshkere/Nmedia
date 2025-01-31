package ru.tatalaraydar.nmedia.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.tatalaraydar.nmedia.dao.PostDao
import ru.tatalaraydar.nmedia.dto.Post
import kotlin.math.floor

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {

    private var nextId = 1L

    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

//    init {
//        posts = dao.getAll()
//        data.value = posts
//    }

    override fun getAll(): MutableLiveData<List<Post>> = data

    override fun save(post: Post) {
//        val id = post.id
//        val saved = dao.save(post)
//        posts = if (id == 0L) {
//            listOf(saved) + posts
//        } else {
//            posts.map {
//                if (it.id != id) it else saved
//            }
//        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun updateLikeById(id: Long) {
        dao.updateLikeById(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
        data.value = posts
    }

    override fun updateShareById(id: Long) {
        dao.updateShareById(id)
        Log.d("UpdateShare", "Обновление в базе данных прошло успешно.")
        posts = posts.map {
            if (it.id != id) it else it.copy(share = it.share + 1)
        }
        data.value = posts
        Log.d("UpdateShare", "Обновление списка постов прошло успешно. Новый список: $posts")
    }

    companion object {
        private const val KEY = "id"
        private const val ID = "posts"
        private const val FILENAME = "posts.json"
        private val gson = Gson()
        private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type


        fun formatCount(count: Int): String {
            return when {
                count >= 1_000_000 -> String.format("%.1fM", floor(count / 1_000_000.0 * 10) / 10)
                    .replace(",", ".")

                count >= 1_000 -> String.format("%.1fK", floor(count / 1_000.0 * 10) / 10)
                    .replace(",", ".")

                else -> count.toString()
            }
        }
    }
}