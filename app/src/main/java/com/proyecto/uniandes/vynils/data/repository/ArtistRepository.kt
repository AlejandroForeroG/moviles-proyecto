package com.proyecto.uniandes.vynils.data.repository

import com.proyecto.uniandes.vynils.data.model.RequestArtist
import com.proyecto.uniandes.vynils.data.model.ResponseArtist
import com.proyecto.uniandes.vynils.data.network.VinylApiService
import com.proyecto.uniandes.vynils.data.network.safeApiCall
import javax.inject.Inject

open class ArtistRepository @Inject constructor(private val api: VinylApiService) {
    open suspend fun getAllArtists(): Result<List<ResponseArtist>> = safeApiCall { api.getAllArtist() }
    open suspend fun createArtist(artist: RequestArtist): Result<ResponseArtist> = safeApiCall {
        api.createArtist(artist)
    }
}