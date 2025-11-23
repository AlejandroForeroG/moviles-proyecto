package com.proyecto.uniandes.vynils.data.model

import com.google.gson.annotations.SerializedName

data class RequestComment (
    @SerializedName("description") val description: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("collector") val collector: Map<String, Int>
)
