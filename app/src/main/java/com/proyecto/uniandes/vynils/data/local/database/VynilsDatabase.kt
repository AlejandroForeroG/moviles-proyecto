package com.proyecto.uniandes.vynils.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto.uniandes.vynils.data.local.dao.AlbumDao
import com.proyecto.uniandes.vynils.data.local.dao.ArtistDao
import com.proyecto.uniandes.vynils.data.local.dao.UserDao
import com.proyecto.uniandes.vynils.data.local.entity.AlbumEntity
import com.proyecto.uniandes.vynils.data.local.entity.ArtistEntity
import com.proyecto.uniandes.vynils.data.local.entity.UserEntity

@Database(entities = [UserEntity::class, AlbumEntity::class, ArtistEntity::class], version = 3, exportSchema = false)
abstract class VynilsDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
}