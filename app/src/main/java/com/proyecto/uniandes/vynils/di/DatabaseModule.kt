package com.proyecto.uniandes.vynils.di

import android.content.Context
import androidx.room.Room
import com.proyecto.uniandes.vynils.data.local.database.VynilsDatabase
import com.proyecto.uniandes.vynils.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VynilsDatabase {
        return Room.databaseBuilder(
            context,
            VynilsDatabase::class.java,
            "vynils_database"
        ).build()
    }

    @Provides
    fun provideUserDao(database: VynilsDatabase): UserDao {
        return database.userDao()
    }
}