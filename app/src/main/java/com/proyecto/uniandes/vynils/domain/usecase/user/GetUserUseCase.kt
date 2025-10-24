package com.proyecto.uniandes.vynils.domain.usecase.user

import com.proyecto.uniandes.vynils.data.local.entity.UserEntity
import com.proyecto.uniandes.vynils.data.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(): UserEntity? = repository.getUser()
}