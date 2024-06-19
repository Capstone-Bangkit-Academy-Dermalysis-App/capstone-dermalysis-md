package com.dermalisys.ui.resetpassword

import androidx.lifecycle.ViewModel
import com.dermalisys.data.UserRepository

class ResetPasswordViewModel(private val repository: UserRepository) : ViewModel() {

    fun resetPassword(signature: String, email: String) = repository.resetPassword(signature, email)

}