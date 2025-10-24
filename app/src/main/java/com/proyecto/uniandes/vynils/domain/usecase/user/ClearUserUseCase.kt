package com.proyecto.uniandes.vynils.domain.usecase.user

import com.proyecto.uniandes.vynils.data.repository.UserRepository
import javax.inject.Inject

class ClearUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() {
        repository.clearUser()
    }
}