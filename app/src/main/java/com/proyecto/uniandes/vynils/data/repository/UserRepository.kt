package com.proyecto.uniandes.vynils.data.repository

import com.proyecto.uniandes.vynils.data.local.dao.UserDao
import com.proyecto.uniandes.vynils.data.local.entity.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getUser(): UserEntity? {
        return userDao.getUser()
    }

    suspend fun saveUser(userType: String) {
        userDao.insertUser(UserEntity(userType = userType))
    }

    suspend fun clearUser() {
        userDao.clearUser()
    }
}