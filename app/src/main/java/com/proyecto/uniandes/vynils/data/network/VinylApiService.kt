package com.proyecto.uniandes.vynils.data.network

import com.proyecto.uniandes.vynils.data.model.RequestAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseArtist
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VinylApiService {
    @GET("/albums")
    suspend fun getAllAlbums(): Response<List<ResponseAlbum>>

    @POST("/albums")
    suspend fun createAlbum(@Body album: RequestAlbum): Response<ResponseAlbum>

    @GET("/albums/{id}")
    suspend fun getAlbumById(@Path("id") id: Int): Response<ResponseAlbum>

    @GET("/musicians")
    suspend fun getAllArtist(): Response<List<ResponseArtist>>
}