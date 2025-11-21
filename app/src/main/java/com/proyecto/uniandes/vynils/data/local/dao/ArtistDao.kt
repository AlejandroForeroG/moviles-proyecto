package com.proyecto.uniandes.vynils.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.proyecto.uniandes.vynils.data.local.entity.ArtistEntity

@Dao
interface ArtistDao {

    @Query("SELECT * FROM artists WHERE id = :artistId LIMIT 1")
    suspend fun getArtistById(artistId: Int): ArtistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: ArtistEntity)
}

