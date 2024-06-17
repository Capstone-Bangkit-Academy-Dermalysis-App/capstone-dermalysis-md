package com.dermalisys.ui.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dermalisys.data.UserRepository
import com.dermalisys.data.pref.UserModel
import okhttp3.MultipartBody

class PreviewViewModel(private val repository: UserRepository): ViewModel() {

    fun getSession(): LiveData<UserModel> = repository.getSession().asLiveData()
    fun predictWithUser(multipart: MultipartBody.Part, userId: String, token: String, accessToken: String) =
        repository.predictWithUser(multipart, userId, token, accessToken)
    fun predictWithoutUser(multipart: MultipartBody.Part, token: String) = repository.predictWithoutUser(multipart, token)
}