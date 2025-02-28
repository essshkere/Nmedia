package ru.tatalaraydar.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import ru.tatalaraydar.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")

    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity)
//TODO
    // поменять на fun insert(posts: List<PostEntity>)??


    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    fun edit(id: Long, content: String)

    fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else edit(post.id, post.content)

    @Query("""
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """)
    fun updateLikeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Long)



    @Query("""
         UPDATE PostEntity SET
               share = share + 1
           WHERE id = :id
        """)
    fun updateShareById(id: Long)
}
