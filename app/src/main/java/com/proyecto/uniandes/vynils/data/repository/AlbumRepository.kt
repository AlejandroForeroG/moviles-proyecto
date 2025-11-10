package com.proyecto.uniandes.vynils.data.repository

import com.proyecto.uniandes.vynils.data.local.dao.AlbumDao
import com.proyecto.uniandes.vynils.data.local.entity.AlbumEntity
import com.proyecto.uniandes.vynils.data.model.RequestAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.network.VinylApiService
import com.proyecto.uniandes.vynils.data.network.safeApiCall
import javax.inject.Inject

open class AlbumRepository @Inject constructor(private val api: VinylApiService, private val albumDao: AlbumDao) {
    open suspend fun getAllAlbums(): Result<List<ResponseAlbum>> = safeApiCall { api.getAllAlbums() }
    open suspend fun createAlbum(album: RequestAlbum): Result<ResponseAlbum> = safeApiCall {
        api.createAlbum(album)
    }

    open suspend fun getAlbumById(albumId: Int): ResponseAlbum? {
        val cached = albumDao.getAlbumById(albumId)
        return if (cached == null) {
            val albumFromApi = safeApiCall { api.getAlbumById(albumId) }
            val cachedAlbum = albumFromApi.getOrNull()
            if (cachedAlbum != null) {
                albumDao.insertAlbum(
                    AlbumEntity(
                        id = cachedAlbum.id,
                        name = cachedAlbum.name,
                        cover = cachedAlbum.cover,
                        releaseDate = cachedAlbum.releaseDate,
                        description = cachedAlbum.description,
                        genre = cachedAlbum.genre,
                        recordLabel = cachedAlbum.recordLabel
                    )
                )
            }
            cachedAlbum
        } else {
            ResponseAlbum(
                id = cached.id,
                name = cached.name,
                cover = cached.cover,
                releaseDate = cached.releaseDate,
                description = cached.description,
                genre = cached.genre,
                recordLabel = cached.recordLabel
            )
        }
    }
}