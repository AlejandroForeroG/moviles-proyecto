package com.proyecto.uniandes.vynils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.uniandes.vynils.domain.usecase.user.ClearUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val clearUserUseCase: ClearUserUseCase) : ViewModel() {


    fun clearUser(onComplete: () -> Unit) {
        viewModelScope.launch {
            clearUserUseCase()
            onComplete()
        }
    }

}