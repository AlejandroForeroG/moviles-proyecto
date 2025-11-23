package com.proyecto.uniandes.vynils.domain.usecase.comment

import com.proyecto.uniandes.vynils.data.repository.CommentRepository
import javax.inject.Inject

class GetCommentsByAlbumIdUseCase @Inject constructor(private val repository: CommentRepository){
    suspend operator fun invoke(albumId: Int) = repository.getCommentsByAlbumId(albumId)
}