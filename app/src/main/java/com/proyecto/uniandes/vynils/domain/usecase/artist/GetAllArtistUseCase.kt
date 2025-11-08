package com.proyecto.uniandes.vynils.domain.usecase.artist

import com.proyecto.uniandes.vynils.data.repository.ArtistRepository
import javax.inject.Inject

class GetAllArtistUseCase @Inject constructor(private val repository: ArtistRepository) {
    suspend operator fun invoke() = repository.getAllArtists()
}