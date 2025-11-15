package com.proyecto.uniandes.vynils.ui.album.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.domain.usecase.album.GetAlbumByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(private val getAlbumByIdUseCase: GetAlbumByIdUseCase) : ViewModel() {

    private val _selectedAlbum = MutableLiveData<ResponseAlbum>()

    val selectedAlbum = _selectedAlbum

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

}