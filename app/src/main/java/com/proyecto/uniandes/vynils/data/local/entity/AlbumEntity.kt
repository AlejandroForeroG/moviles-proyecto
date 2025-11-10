package com.proyecto.uniandes.vynils.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("albums")
data class AlbumEntity (
    @PrimaryKey val id: Int,
    val name: String,
    val cover: String,
    val releaseDate : String,
    val description: String,
    val genre : String,
    val recordLabel : String
)