package com.proyecto.uniandes.vynils.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto.uniandes.vynils.data.local.dao.AlbumDao
import com.proyecto.uniandes.vynils.data.local.dao.UserDao
import com.proyecto.uniandes.vynils.data.local.entity.AlbumEntity
import com.proyecto.uniandes.vynils.data.local.entity.UserEntity

@Database(entities = [UserEntity::class, AlbumEntity::class], version = 2, exportSchema = false)
abstract class VynilsDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun albumDao(): AlbumDao
}