package com.dermalisys.ui.register

import androidx.lifecycle.ViewModel
import com.dermalisys.data.UserRepository

class RegisterViewModel(private val repository: UserRepository): ViewModel() {

    fun register(email: String, password: String, name: String) =
        repository.register(email, password, name)
}