package com.proyecto.uniandes.vynils.domain.usecase.user

import com.proyecto.uniandes.vynils.data.repository.UserRepository
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userType: String) {
        repository.saveUser(userType)
    }
}