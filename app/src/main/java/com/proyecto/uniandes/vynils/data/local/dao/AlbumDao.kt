package com.proyecto.uniandes.vynils.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.proyecto.uniandes.vynils.data.local.entity.AlbumEntity

@Dao
interface AlbumDao {

    @Query("SELECT * FROM albums WHERE id = :albumId LIMIT 1")
    suspend fun getAlbumById(albumId: Int): AlbumEntity?

    @Insert
    suspend fun insertAlbum(album: AlbumEntity)
}