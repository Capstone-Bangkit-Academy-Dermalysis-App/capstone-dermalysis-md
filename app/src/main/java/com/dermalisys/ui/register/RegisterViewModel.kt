package com.dermalisys.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dermalisys.data.UserRepository
import com.dermalisys.data.pref.UserModel
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository): ViewModel() {

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun register(email: String, password: String, name: String) =
        repository.register(email, password, name)

    fun storeNewUser(signature: String, id: String, name: String, email: String) =
        repository.storeNewUser(signature, id, name, email)
}