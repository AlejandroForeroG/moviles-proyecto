package com.proyecto.uniandes.vynils.data.repository

import com.proyecto.uniandes.vynils.data.model.RequestComment
import com.proyecto.uniandes.vynils.data.model.ResponseComment
import com.proyecto.uniandes.vynils.data.network.VinylApiService
import com.proyecto.uniandes.vynils.data.network.safeApiCall
import javax.inject.Inject

open class CommentRepository @Inject constructor(private val api: VinylApiService) {
    open suspend fun getCommentsByAlbumId(albumId: Int) : Result<List<ResponseComment>> = safeApiCall {
        api.getAlbumComments(albumId)
    }
    open suspend fun createComment(albumId: Int, comment: RequestComment): Result<ResponseComment> = safeApiCall {
        api.createComment(albumId, comment) }
}