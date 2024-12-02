package ru.tatalaraydar.nmedia.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.tatalaraydar.nmedia.dto.Post
import kotlin.math.floor

class PostRepositorySharedPrefs(
    context: Context,
) : PostRepository {

    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)

    private var nextId = 1L

    private var posts = listOf(
        Post(
            id = 9,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Освоение новой профессии — это не только открывающиеся возможности и перспективы, но и настоящий вызов самому себе. Приходится выходить из зоны комфорта и перестраивать привычный образ жизни: менять распорядок дня, искать время для занятий, быть готовым к возможным неудачам в начале пути. В блоге рассказали, как избежать стресса на курсах профпереподготовки → http://netolo.gy/fPD",
            published = "23 сентября в 10:12",
            likedByMe = false,
            videoURL = "https://www.youtube.com/watch?v=WhWc3b3KhnY"
        ),
        Post(
            id = 8,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Делиться впечатлениями о любимых фильмах легко, а что если рассказать так, чтобы все заскучали \uD83D\uDE34\n",
            published = "22 сентября в 10:14",
            likedByMe = false
        ),
        Post(
            id = 7,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Таймбоксинг — отличный способ навести порядок в своём календаре и разобраться с делами, которые долго откладывали на потом. Его главный принцип — на каждое дело заранее выделяется определённый отрезок времени. В это время вы работаете только над одной задачей, не переключаясь на другие. Собрали советы, которые помогут внедрить таймбоксинг \uD83D\uDC47\uD83C\uDFFB",
            published = "22 сентября в 10:12",
            likedByMe = false
        ),
        Post(
            id = 6,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "\uD83D\uDE80 24 сентября стартует новый поток бесплатного курса «Диджитал-старт: первый шаг к востребованной профессии» — за две недели вы попробуете себя в разных профессиях и определите, что подходит именно вам → http://netolo.gy/fQ",
            published = "21 сентября в 10:12",
            likedByMe = false
        ),
        Post(
            id = 5,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Диджитал давно стал частью нашей жизни: мы общаемся в социальных сетях и мессенджерах, заказываем еду, такси и оплачиваем счета через приложения.",
            published = "20 сентября в 10:14",
            likedByMe = false
        ),
        Post(
            id = 4,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Большая афиша мероприятий осени: конференции, выставки и хакатоны для жителей Москвы, Ульяновска и Новосибирска \uD83D\uDE09",
            published = "19 сентября в 14:12",
            likedByMe = false
        ),
        Post(
            id = 3,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Языков программирования много, и выбрать какой-то один бывает нелегко. Собрали подборку статей, которая поможет вам начать, если вы остановили свой выбор на JavaScript.",
            published = "19 сентября в 10:24",
            likedByMe = false
        ),
        Post(
            id = 2,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Знаний хватит на всех: на следующей неделе разбираемся с разработкой мобильных приложений, учимся рассказывать истории и составлять PR-стратегию прямо на бесплатных занятиях \uD83D\uDC47",
            published = "18 сентября в 10:12",
            likedByMe = false
        ),
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false
        ),
    )
        set(value) {
            field = value
            sync()
        }

    private val data = MutableLiveData(posts)

    override fun getAll(): MutableLiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(likedByMe = !it.likedByMe)
        }
        data.value = posts
        sync()
    }

    override fun updateLikeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (!it.likedByMe) it.likes + 1 else it.likes - 1
            )
        }
        data.value = posts
    }

    override fun updateShareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(share = it.share + 1)
        }
        data.value = posts
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    likedByMe = false,
                    published = "now"
                )
            ) + posts
            data.value = posts
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    init {
        prefs.getString(KEY, null)?.let {
            posts = gson.fromJson(it, type)
            data.value = posts
            nextId = prefs.getLong(ID, nextId)
        }
        sync()
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

    private fun sync() {
        prefs.edit().apply {
            putString(KEY, gson.toJson(posts))
            putLong(ID, nextId)
            apply()
        }
    }
}