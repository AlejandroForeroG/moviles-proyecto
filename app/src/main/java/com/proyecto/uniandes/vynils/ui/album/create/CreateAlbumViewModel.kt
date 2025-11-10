package com.proyecto.uniandes.vynils.ui.album.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.uniandes.vynils.data.model.RequestAlbum
import com.proyecto.uniandes.vynils.domain.usecase.album.CreateAlbumUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateAlbumViewModel @Inject constructor(private val createAlbumUseCase: CreateAlbumUseCase): ViewModel() {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess


    fun createAlbum(album: RequestAlbum) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val response = createAlbumUseCase(album)
                if (response.isSuccess) {
                    _isSuccess.postValue(true)
                } else {
                    _isSuccess.postValue(false)
                }
            }
        }
    }
}