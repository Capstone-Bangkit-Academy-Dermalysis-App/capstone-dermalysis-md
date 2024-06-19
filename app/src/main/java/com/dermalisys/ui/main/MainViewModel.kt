package com.dermalisys.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dermalisys.data.UserRepository
import com.dermalisys.data.pref.UserModel
import com.dermalisys.data.remote.response.getuserpredict.DataItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout(accessToken: String, signature: String) {
        viewModelScope.launch {
            repository.logout()
        }
        repository.logoutApi(accessToken, signature)
    }

    fun getHistory(
        signature: String, userId: String, accessToken: String
    ): LiveData<PagingData<DataItem>> = repository.getHistory(signature, userId, accessToken).cachedIn(viewModelScope)

    fun getAccessTokenWithHistoryAPI(signature: String, userId: String, accessToken: String) =
        repository.getAccessTokenWithHistoryAPI(signature, userId, accessToken)
}