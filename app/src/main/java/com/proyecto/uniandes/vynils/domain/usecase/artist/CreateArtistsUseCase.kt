package com.proyecto.uniandes.vynils.domain.usecase.artist

import com.proyecto.uniandes.vynils.data.model.RequestArtist
import com.proyecto.uniandes.vynils.data.repository.ArtistRepository
import javax.inject.Inject

class CreateArtistUseCase @Inject constructor(private val repository: ArtistRepository)  {
    suspend operator fun invoke(artistRequest: RequestArtist) = repository.createArtist(artistRequest)
}