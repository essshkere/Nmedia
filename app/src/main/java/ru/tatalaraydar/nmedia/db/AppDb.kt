package ru.tatalaraydar.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.tatalaraydar.nmedia.dao.PostDao
import ru.tatalaraydar.nmedia.dao.PostRemoteKeyDao
import ru.tatalaraydar.nmedia.entity.PostEntity
import ru.tatalaraydar.nmedia.entity.PostRemoteKeyEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}

