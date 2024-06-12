package com.dermalisys.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dermalisys.BuildConfig
import com.dermalisys.data.pref.UserModel
import com.dermalisys.data.pref.UserPreferences
import com.dermalisys.data.remote.response.ErrorResponse
import com.dermalisys.data.remote.response.login.LoginBadRequestResponse
import com.dermalisys.data.remote.response.login.LoginOkResponse
import com.dermalisys.data.remote.response.register.RegisterOkResponse
import com.dermalisys.data.remote.retrofit.ApiConfig
import com.dermalisys.data.remote.retrofit.ApiService
import com.dermalisys.util.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class UserRepository(
    private val apiservice: ApiService,
    private val userPref: UserPreferences
) {
    private val secretToken = BuildConfig.API_SECRET_TOKEN

    suspend fun saveSession(user: UserModel) {
        userPref.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPref.getSession()
    }

    suspend fun logout() {
        userPref.logout()
    }

    // Function to generate signature
    private fun generateSignature(data: String, secretToken: String): String {
        val algorithm = "HmacSHA256"
        val mac = Mac.getInstance(algorithm)
        val keySpec = SecretKeySpec(secretToken.toByteArray(), algorithm)
        mac.init(keySpec)
        val hash = mac.doFinal(data.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun register(
        email: String, pass: String, name: String
    ): LiveData<Result<RegisterOkResponse>> = liveData  {

        // Request data
        val jsonData = "{\"email\":\"$email\",\"password\":\"$pass\",\"name\":\"$name\"}"

        // Compute signature
        val signature = generateSignature(jsonData, secretToken)
        emit(Result.Loading)
        try {
            val response = ApiConfig.getApiService(signature).register(email, pass, name)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.d("register", e.message.toString())
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        }
    }

    fun login(
        email: String, pass: String
    ): LiveData<Result<LoginOkResponse>> = liveData  {
        val jsonData = "{\"email\":\"$email\",\"password\":\"$pass\"}"

        val signature = generateSignature(jsonData, secretToken)

        Log.d("signature", signature)
        emit(Result.Loading)
        try {
            val response = ApiConfig.getApiService(signature).login(email, pass)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.d("login", e.message.toString())
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage!!))
        }
    }

    companion object{
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPref: UserPreferences
        ): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPref)
            }.also { instance = it }
        }
    }
}