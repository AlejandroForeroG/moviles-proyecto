package com.proyecto.uniandes.vynils.ui.album.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.uniandes.vynils.data.model.RequestComment
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseComment
import com.proyecto.uniandes.vynils.domain.usecase.album.GetAlbumByIdUseCase
import com.proyecto.uniandes.vynils.domain.usecase.comment.CreateCommentUseCase
import com.proyecto.uniandes.vynils.domain.usecase.comment.GetCommentsByAlbumIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(private val getAlbumByIdUseCase: GetAlbumByIdUseCase,
    private val getCommentsUseCase: GetCommentsByAlbumIdUseCase,
    private val addCommentUseCase: CreateCommentUseCase
) : ViewModel() {

    private val _selectedAlbum = MutableLiveData<ResponseAlbum>()

    val selectedAlbum = _selectedAlbum

    private val _comments = MutableLiveData<List<ResponseComment>>()

    val comments = _comments

    private val _addCommentSuccess = MutableLiveData<Boolean>()

    val addCommentSuccess = _addCommentSuccess

    fun getAlbumById(albumId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getAlbumByIdUseCase(albumId)
                result?.let {
                    _selectedAlbum.postValue(it)
                    Log.d("AlbumViewModel", "Album loaded: ${it.name}")
                } ?: run {
                    Log.e("AlbumViewModel", "Album with id $albumId not found")
                }
            }
        }
    }

    fun getComments(albumId: Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getCommentsUseCase(albumId)
                val commentsList = result.getOrNull()
                commentsList?.let {
                    _comments.postValue(it)
                    Log.d("AlbumDetailViewModel", "Comments loaded: ${it.size}")
                } ?: run {
                    _comments.postValue(emptyList())
                    Log.e("AlbumDetailViewModel", "No comments found: ${result.exceptionOrNull()}")
                }
            }
        }
    }

    fun addComment(albumId: Int, comment: RequestComment) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = addCommentUseCase(albumId, comment)
                result.fold(
                    onSuccess = { responseComment ->
                        _addCommentSuccess.postValue(true)
                        Log.d("AlbumDetailViewModel", "Comentario añadido exitosamente: ${responseComment.id}")
                        getComments(albumId)
                    },
                    onFailure = { exception ->
                        _addCommentSuccess.postValue(false)
                        Log.e("AlbumDetailViewModel", "Fallo al añadir comentario: ${exception.message}")
                    }
                )
            }
        }
    }

}