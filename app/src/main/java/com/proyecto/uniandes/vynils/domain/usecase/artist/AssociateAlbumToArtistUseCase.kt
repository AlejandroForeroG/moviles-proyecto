package com.proyecto.uniandes.vynils.domain.usecase.artist

import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.repository.ArtistRepository
import javax.inject.Inject

class AssociateAlbumToArtistUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) {
    suspend operator fun invoke(musicianId: Int, albumId: Int): ResponseAlbum? {
        val result = artistRepository.associateAlbumToArtist(musicianId, albumId)
        return result.getOrNull()
    }
}