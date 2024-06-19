package com.dermalisys.ui.editprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dermalisys.data.UserRepository
import com.dermalisys.data.pref.UserModel

class EditProfileViewModel(private val repository: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> =
        repository.getSession().asLiveData()

    fun updateUserDisplatName(signature: String, userId: String, name: String, accessToken: String) =
        repository.updateUserDisplayName(signature, userId, name, accessToken)

    fun resetPassword(signature: String, email: String) =
        repository.resetPassword(signature, email)
}