package com.proyecto.uniandes.vynils.domain.usecase.comment

import com.proyecto.uniandes.vynils.data.model.RequestComment
import com.proyecto.uniandes.vynils.data.repository.CommentRepository
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(private val repository: CommentRepository){
    suspend operator fun invoke(albumId: Int, requestComment: RequestComment) = repository.createComment(albumId, requestComment)
}