package com.proyecto.uniandes.vynils.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `albums` (
                `id` INTEGER PRIMARY KEY NOT NULL,
                `name` TEXT,
                `cover` TEXT,
                `releaseDate` TEXT,
                `description` TEXT,
                `genre` TEXT,
                `recordLabel` TEXT
            )
            """.trimIndent()
            )
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `artists` (
                `id` INTEGER PRIMARY KEY NOT NULL,
                `name` TEXT NOT NULL,
                `image` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `birthDate` TEXT NOT NULL
            )
            """.trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VynilsDatabase {
        return Room.databaseBuilder(
            context,
            VynilsDatabase::class.java,
            "vynils_database"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideUserDao(database: VynilsDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideAlbumDao(database: VynilsDatabase) = database.albumDao()

    @Provides
    fun provideArtistDao(database: VynilsDatabase) = database.artistDao()
}