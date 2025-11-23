package com.proyecto.uniandes.vynils.data.network

import com.proyecto.uniandes.vynils.data.model.RequestAlbum
import com.proyecto.uniandes.vynils.data.model.RequestArtist
import com.proyecto.uniandes.vynils.data.model.RequestComment
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseArtist
import com.proyecto.uniandes.vynils.data.model.ResponseComment
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

    @GET("/musicians/{id}")
    suspend fun getArtistById(@Path("id") id: Int): Response<ResponseArtist>

    @POST("/musicians")
    suspend fun createArtist(@Body artist: RequestArtist): Response<ResponseArtist>

    @GET("/albums/{albumId}/comments")
    suspend fun getAlbumComments(@Path("albumId") albumId: Int): Response<List<ResponseComment>>

    @POST("/albums/{albumId}/comments")
    suspend fun createComment(@Path("albumId") albumId: Int, @Body comment: RequestComment): Response<ResponseComment>
}