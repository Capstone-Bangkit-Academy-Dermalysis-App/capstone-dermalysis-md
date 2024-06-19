package com.dermalisys.data.di

import android.content.Context
import com.dermalisys.data.UserRepository
import com.dermalisys.data.database.HistoryDatabase
import com.dermalisys.data.pref.UserPreferences
import com.dermalisys.data.pref.datastore
import com.dermalisys.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreferences.getInstance(context.datastore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.accessToken)
        val database = HistoryDatabase.getDatabase(context)
        return UserRepository.getInstance(apiService, pref, database)
    }
}