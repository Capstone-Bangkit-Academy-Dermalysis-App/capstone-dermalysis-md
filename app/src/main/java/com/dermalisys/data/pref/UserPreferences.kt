package com.dermalisys.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "session")
class UserPreferences private constructor(private val datastore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        datastore.edit {
            it[EMAIL] = user.email
            it[TOKEN] = user.token
            it[NAME] = user.name
            it[USERID] = user.userId
            it[ACCESSTOKEN] = user.accessToken
            it[IS_LOGIN] = true
        }
    }

    fun getSession(): Flow<UserModel> {
        return datastore.data.map {
            UserModel(
                it[EMAIL] ?: "",
                it[TOKEN] ?: "",
                it[NAME] ?: "",
                it[USERID] ?: "",
                it[ACCESSTOKEN] ?: "",
                it[IS_LOGIN] ?: false
            )
        }
    }

    suspend fun logout() {
        datastore.edit {
            it.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences ?= null

        private val EMAIL = stringPreferencesKey("name")
        private val TOKEN = stringPreferencesKey("token")
        private val NAME = stringPreferencesKey("name")
        private val USERID = stringPreferencesKey("userId")
        private val ACCESSTOKEN = stringPreferencesKey("accessToken")
        private val IS_LOGIN = booleanPreferencesKey("isLogin")

        fun getInstance(datastore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(datastore)
                INSTANCE = instance
                instance
            }
        }
    }
}