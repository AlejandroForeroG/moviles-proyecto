package com.proyecto.uniandes.vynils.ui.artist.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.uniandes.vynils.data.local.entity.UserEntity
import com.proyecto.uniandes.vynils.data.model.ResponseArtist
import com.proyecto.uniandes.vynils.domain.usecase.artist.GetAllArtistUseCase
import com.proyecto.uniandes.vynils.domain.usecase.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(private val getAllArtistUseCase: GetAllArtistUseCase, private val getUserUseCase: GetUserUseCase) : ViewModel() {
    private val _user = MutableLiveData<UserEntity?>()
    val user: LiveData<UserEntity?>
        get() = _user

    private val _albums = MutableLiveData<List<ResponseArtist>>()
    val albums: LiveData<List<ResponseArtist>> = _albums

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            _user.postValue(getUserUseCase())
        }
    }

    fun getAllArtist() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getAllArtistUseCase()
                result.fold(onSuccess = { list ->
                    _albums.postValue(list)
                }, onFailure = { ex ->
                    _albums.postValue(emptyList())
                })
            }
        }
    }

}