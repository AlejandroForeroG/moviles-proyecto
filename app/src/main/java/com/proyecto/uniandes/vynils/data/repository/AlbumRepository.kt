package com.proyecto.uniandes.vynils.data.repository

import com.proyecto.uniandes.vynils.data.model.Album
import com.proyecto.uniandes.vynils.data.network.VinylApiService
import com.proyecto.uniandes.vynils.data.network.safeApiCall
import javax.inject.Inject

open class AlbumRepository @Inject constructor(private val api: VinylApiService) {
    open suspend fun getAllAlbums(): Result<List<Album>> = safeApiCall { api.getAllAlbums() }
}