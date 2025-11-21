package com.proyecto.uniandes.vynils.domain.usecase.artist

import com.proyecto.uniandes.vynils.data.repository.ArtistRepository
import javax.inject.Inject

class GetArtistByIdUseCase @Inject constructor(private val repository: ArtistRepository) {
    suspend operator fun invoke(artistId: Int) = repository.getArtistById(artistId)
}

