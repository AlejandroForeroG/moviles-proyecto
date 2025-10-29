package com.proyecto.uniandes.vynils.data.repository

import com.proyecto.uniandes.vynils.data.model.RequestAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.network.VinylApiService
import com.proyecto.uniandes.vynils.data.network.safeApiCall
import javax.inject.Inject

open class AlbumRepository @Inject constructor(private val api: VinylApiService) {
    open suspend fun getAllAlbums(): Result<List<ResponseAlbum>> = safeApiCall { api.getAllAlbums() }
    open suspend fun createAlbum(album: RequestAlbum): Result<ResponseAlbum> = safeApiCall {
        api.createAlbum(album)
    }
}