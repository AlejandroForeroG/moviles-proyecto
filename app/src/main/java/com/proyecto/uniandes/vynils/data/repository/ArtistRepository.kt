package com.proyecto.uniandes.vynils.data.repository

import com.proyecto.uniandes.vynils.data.local.dao.ArtistDao
import com.proyecto.uniandes.vynils.data.local.entity.ArtistEntity
import com.proyecto.uniandes.vynils.data.model.RequestArtist
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseArtist
import com.proyecto.uniandes.vynils.data.network.VinylApiService
import com.proyecto.uniandes.vynils.data.network.safeApiCall
import javax.inject.Inject

open class ArtistRepository @Inject constructor(
    private val api: VinylApiService,
    private val artistDao: ArtistDao
) {
    open suspend fun getAllArtists(): Result<List<ResponseArtist>> = safeApiCall { api.getAllArtist() }

    open suspend fun getArtistById(id: Int): ResponseArtist? {
        val cached = artistDao.getArtistById(id)
        return if (cached == null) {
            val artistFromApi = safeApiCall { api.getArtistById(id) }
            val cachedArtist = artistFromApi.getOrNull()
            if (cachedArtist != null) {
                artistDao.insertArtist(
                    ArtistEntity(
                        id = cachedArtist.id ?: 0,
                        name = cachedArtist.name ?: "",
                        image = cachedArtist.image ?: "",
                        description = cachedArtist.description ?: "",
                        birthDate = cachedArtist.birthDate ?: ""
                    )
                )
            }
            cachedArtist
        } else {
            ResponseArtist(
                id = cached.id,
                name = cached.name,
                image = cached.image,
                description = cached.description,
                birthDate = cached.birthDate
            )
        }
    }

    open suspend fun createArtist(artist: RequestArtist): Result<ResponseArtist> = safeApiCall {
        api.createArtist(artist)
    }

    open suspend fun getArtistAlbums(musicianId: Int): Result<List<ResponseAlbum>> = safeApiCall {
        api.getArtistAlbums(musicianId)
    }

    open suspend fun associateAlbumToArtist(musicianId: Int, albumId: Int): Result<ResponseAlbum> = safeApiCall {
        api.associateAlbumToArtist(musicianId, albumId)
    }
}