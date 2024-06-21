package com.dermalisys.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dermalisys.data.UserRepository
import com.dermalisys.data.pref.UserModel

class ResultViewModel(private val repository: UserRepository): ViewModel() {

    fun getSesssion(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}