package com.proyecto.uniandes.vynils.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("artists")
data class ArtistEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val birthDate: String
)

