package com.proyecto.uniandes.vynils.ui.artist.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseArtist
import com.proyecto.uniandes.vynils.domain.usecase.album.GetAllAlbumUseCase
import com.proyecto.uniandes.vynils.domain.usecase.artist.AssociateAlbumToArtistUseCase
import com.proyecto.uniandes.vynils.domain.usecase.artist.GetArtistAlbumsUseCase
import com.proyecto.uniandes.vynils.domain.usecase.artist.GetArtistByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val getArtistByIdUseCase: GetArtistByIdUseCase,
    private val getAllAlbumUseCase: GetAllAlbumUseCase,
    private val getArtistAlbumsUseCase: GetArtistAlbumsUseCase,
    private val associateAlbumToArtistUseCase: AssociateAlbumToArtistUseCase
) : ViewModel() {

    private val _selectedArtist = MutableLiveData<ResponseArtist>()
    val selectedArtist = _selectedArtist

    private val _allAlbums = MutableLiveData<List<ResponseAlbum>>()
    val allAlbums = _allAlbums

    private val _artistAlbums = MutableLiveData<List<ResponseAlbum>>()
    val artistAlbums = _artistAlbums

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage = _errorMessage

    private val _associationSuccess = MutableLiveData<Boolean>()
    val associationSuccess = _associationSuccess

    private val _availableAlbums = MutableLiveData<List<ResponseAlbum>>()
    val availableAlbums = _availableAlbums

    fun getArtistById(artistId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getArtistByIdUseCase(artistId)
                result?.let {
                    _selectedArtist.postValue(it)
                    Log.d("ArtistDetailViewModel", "Artist loaded: ${it.name}")
                    loadArtistAlbums(artistId)
                } ?: run {
                    Log.e("ArtistDetailViewModel", "Artist with id $artistId not found")
                }
            }
        }
    }

    fun loadArtistAlbums(artistId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getArtistAlbumsUseCase(artistId)
                result?.let {
                    _artistAlbums.postValue(it)
                    Log.d("ArtistDetailViewModel", "Artist albums loaded: ${it.size}")
                } ?: run {
                    _artistAlbums.postValue(emptyList())
                    Log.d("ArtistDetailViewModel", "No albums found for artist")
                }
            }
        }
    }

    fun associateAlbum(artistId: Int, albumId: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            withContext(Dispatchers.IO) {
                val result = associateAlbumToArtistUseCase(artistId, albumId)
                result?.let {
                    Log.d("ArtistDetailViewModel", "Album associated successfully")
                    _associationSuccess.postValue(true)
                    loadArtistAlbums(artistId)
                } ?: run {
                    Log.e("ArtistDetailViewModel", "Failed to associate album")
                    _errorMessage.postValue("Error al asociar el álbum")
                    _associationSuccess.postValue(false)
                }
                _isLoading.postValue(false)
            }
        }
    }

    fun loadAvailableAlbums(artistId: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            withContext(Dispatchers.IO) {
                val allAlbumsResult = getAllAlbumUseCase()
                val artistAlbumsResult = getArtistAlbumsUseCase(artistId)

                val allAlbums = allAlbumsResult.getOrNull() ?: emptyList()
                val artistAlbums = artistAlbumsResult ?: emptyList()

                val artistAlbumIds = artistAlbums.map { it.id }.toSet()
                val available = allAlbums.filter { it.id !in artistAlbumIds }

                if (available.isEmpty()) {
                    _errorMessage.postValue("Todos los álbumes ya están asociados")
                } else {
                    _availableAlbums.postValue(available)
                }

                _isLoading.postValue(false)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearAssociationSuccess() {
        _associationSuccess.value = false
    }
}