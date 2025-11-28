package com.proyecto.uniandes.vynils.domain.usecase.artist

import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.repository.ArtistRepository
import javax.inject.Inject

class GetArtistAlbumsUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) {
    suspend operator

    fun invoke(musicianId: Int): List<ResponseAlbum>? {
        val result = artistRepository.getArtistAlbums(musicianId)
        return result.getOrNull()
    }
}