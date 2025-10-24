package com.proyecto.uniandes.vynils.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proyecto.uniandes.vynils.data.local.dao.UserDao
import com.proyecto.uniandes.vynils.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class VynilsDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}