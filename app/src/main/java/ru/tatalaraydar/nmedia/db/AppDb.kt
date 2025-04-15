package ru.tatalaraydar.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.tatalaraydar.nmedia.dao.PostDao
import ru.tatalaraydar.nmedia.entity.PostEntity

@Database(entities = [PostEntity::class], version = 2)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
}

