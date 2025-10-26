package com.proyecto.uniandes.vynils.data.network

import com.proyecto.uniandes.vynils.data.model.Album
import retrofit2.Response
import retrofit2.http.GET

interface VinylApiService {
    @GET("/albums")
    suspend fun getAllAlbums(): Response<List<Album>>
}