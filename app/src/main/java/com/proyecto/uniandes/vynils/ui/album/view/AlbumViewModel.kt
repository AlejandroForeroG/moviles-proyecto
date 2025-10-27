package com.proyecto.uniandes.vynils.ui.album.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.uniandes.vynils.data.local.entity.UserEntity
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.domain.usecase.album.GetAllAlbumUseCase
import com.proyecto.uniandes.vynils.domain.usecase.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(private val getAllAlbumUseCase: GetAllAlbumUseCase, private val getUserUseCase: GetUserUseCase) : ViewModel() {
    private val _user = MutableLiveData<UserEntity?>()
    val user: LiveData<UserEntity?>
        get() = _user

    private val _albums = MutableLiveData<List<ResponseAlbum>>()
    val albums: LiveData<List<ResponseAlbum>> = _albums

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            _user.postValue(getUserUseCase())
        }
    }

    fun getAllAlbums() {
        viewModelScope.launch {
            val result = getAllAlbumUseCase()
            result.fold(onSuccess = { list ->
                _albums.postValue(list)
                Log.d("AlbumViewModel", "Albums loaded: ${list.size}")
            }, onFailure = { ex ->
                Log.e("AlbumViewModel", "Error loading albums", ex)
                _albums.postValue(emptyList())
            })
        }
    }

}