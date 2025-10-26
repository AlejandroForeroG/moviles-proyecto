package com.proyecto.uniandes.vynils.domain.usecase.album

import com.proyecto.uniandes.vynils.data.repository.AlbumRepository
import javax.inject.Inject

class GetAllAlbumUseCase @Inject constructor(private val repository: AlbumRepository) {
    suspend operator fun invoke() = repository.getAllAlbums()
}