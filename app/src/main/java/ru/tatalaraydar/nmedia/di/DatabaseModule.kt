package ru.tatalaraydar.nmedia.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tatalaraydar.nmedia.db.AppDb
import javax.inject.Singleton
import androidx.room.Room
import ru.tatalaraydar.nmedia.dao.PostRemoteKeyDao

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDb {
        return Room.databaseBuilder(context, AppDb::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    fun providePostDao(appDb: AppDb) = appDb.postDao()

    @Provides
    fun providePostRemoteKeyDao(appDb: AppDb): PostRemoteKeyDao = appDb.postRemoteKeyDao()

}