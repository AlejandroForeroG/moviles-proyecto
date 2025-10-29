package com.proyecto.uniandes.vynils.data.model

import kotlinx.serialization.SerialName

data class RequestAlbum(
    @SerialName("name") val name: String,
    @SerialName("cover") val cover: String,
    @SerialName("releaseDate") val releaseDate: String,
    @SerialName("description") val description: String,
    @SerialName("genre") val genre: String,
    @SerialName("recordLabel") val recordLabel: String
)
