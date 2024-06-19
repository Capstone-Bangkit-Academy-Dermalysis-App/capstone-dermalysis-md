package com.dermalisys.ui.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dermalisys.data.UserRepository
import com.dermalisys.data.pref.UserModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class PreviewViewModel(private val repository: UserRepository): ViewModel() {

    fun getSession(): LiveData<UserModel> = repository.getSession().asLiveData()

    fun logout(accessToken: String, signature: String) {
        viewModelScope.launch {
            repository.logout()
        }
        repository.logoutApi(accessToken, signature)
    }

    fun predictWithUser(multipart: MultipartBody.Part, userId: String, token: String, accessToken: String) =
        repository.predictWithUser(multipart, userId, token, accessToken)

    fun predictWithoutUser(multipart: MultipartBody.Part, signature: String) = repository.predictWithoutUser(multipart, signature)
}