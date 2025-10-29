package com.proyecto.uniandes.vynils.domain.usecase.album

import com.proyecto.uniandes.vynils.data.model.RequestAlbum
import com.proyecto.uniandes.vynils.data.repository.AlbumRepository
import javax.inject.Inject

class CreateAlbumUseCase @Inject constructor(private val repository: AlbumRepository) {
    suspend operator fun invoke(albumRequest: RequestAlbum) = repository.createAlbum(albumRequest)
}