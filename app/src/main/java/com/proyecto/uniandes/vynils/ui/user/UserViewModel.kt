package com.proyecto.uniandes.vynils.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.uniandes.vynils.data.local.entity.UserEntity
import com.proyecto.uniandes.vynils.domain.usecase.user.GetUserUseCase
import com.proyecto.uniandes.vynils.domain.usecase.user.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val getUserUseCase: GetUserUseCase, private val saveUserUseCase: SaveUserUseCase): ViewModel() {

    private val _gotoMain: MutableLiveData<Boolean> = MutableLiveData(false)
    val gotoMain: LiveData<Boolean>
        get() = _gotoMain

    private val _user = MutableLiveData<UserEntity?>()
    val user: LiveData<UserEntity?>
        get() = _user

    fun getUser(onComplete: (UserEntity?) -> Unit) {
        viewModelScope.launch {
            onComplete(getUserUseCase())
        }
    }

    fun saveUser(userType: String) {
        viewModelScope.launch {
            saveUserUseCase(userType)
            _gotoMain.postValue(true)
        }
    }
}