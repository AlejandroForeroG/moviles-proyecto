package com.proyecto.uniandes.vynils.ui.artist.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.uniandes.vynils.data.model.RequestArtist
import com.proyecto.uniandes.vynils.domain.usecase.artist.CreateArtistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateArtistViewModel @Inject constructor(private val createArtistUseCase: CreateArtistUseCase): ViewModel() {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    fun createArtist(artist: RequestArtist) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val response = createArtistUseCase(artist)
                if (response.isSuccess) {
                    _isSuccess.postValue(true)
                } else {
                    _isSuccess.postValue(false)
                }
            }
        }
    }
}