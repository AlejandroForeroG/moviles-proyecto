package com.proyecto.uniandes.vynils.ui.artist.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.uniandes.vynils.data.model.ResponseArtist
import com.proyecto.uniandes.vynils.domain.usecase.artist.GetArtistByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val getArtistByIdUseCase: GetArtistByIdUseCase
) : ViewModel() {

    private val _selectedArtist = MutableLiveData<ResponseArtist>()
    val selectedArtist = _selectedArtist

    fun getArtistById(artistId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getArtistByIdUseCase(artistId)
                result?.let {
                    _selectedArtist.postValue(it)
                    Log.d("ArtistDetailViewModel", "Artist loaded: ${it.name}")
                } ?: run {
                    Log.e("ArtistDetailViewModel", "Artist with id $artistId not found")
                }
            }
        }
    }
}

