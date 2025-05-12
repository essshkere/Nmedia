package ru.tatalaraydar.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

import ru.tatalaraydar.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE isVisible = 1 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Query("UPDATE PostEntity SET isVisible = 1")
    suspend fun makeAllPostsVisible()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Query("DELETE FROM PostEntity")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun edit(id: Long, content: String)

    suspend fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else edit(post.id, post.content)

    @Query("""
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """)
    suspend fun updateLikeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend  fun removeById(id: Long)

    @Query("SELECT likedByMe FROM PostEntity WHERE id = :id")
    suspend fun getLikeStateById(id: Long): Boolean?

    @Query("UPDATE PostEntity SET likedByMe = :likedByMe WHERE id = :id")
    suspend fun updateLikeState(id: Long, likedByMe: Boolean)


    @Query("""
         UPDATE PostEntity SET
               share = share + 1
           WHERE id = :id
        """)
    suspend fun updateShareById(id: Long)
}
