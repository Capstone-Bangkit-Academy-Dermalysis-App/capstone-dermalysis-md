package com.dermalisys.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dermalisys.BuildConfig
import com.dermalisys.data.database.HistoryDatabase
import com.dermalisys.data.pref.UserModel
import com.dermalisys.data.pref.UserPreferences
import com.dermalisys.data.remote.response.getuserpredict.DataItem
import com.dermalisys.data.remote.response.getuserpredict.GetAllUserPredictErrorResponse
import com.dermalisys.data.remote.response.getuserpredict.GetAllUserPredictResponse
import com.dermalisys.data.remote.response.login.LoginOkResponse
import com.dermalisys.data.remote.response.logout.LogoutResponse
import com.dermalisys.data.remote.response.predict.PredictWithUserResponse
import com.dermalisys.data.remote.response.register.RegisterBadRequestResponse
import com.dermalisys.data.remote.response.register.RegisterOkResponse
import com.dermalisys.data.remote.retrofit.ApiConfig
import com.dermalisys.data.remote.retrofit.ApiService
import com.dermalisys.util.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.HttpException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class UserRepository(
    private val apiService: ApiService,
    private val userPref: UserPreferences,
    private val historyDatabase: HistoryDatabase
) {
    private val secretToken = BuildConfig.API_SECRET_TOKEN
    private var currentImageUri: Uri? = null

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
            val errorBody = Gson().fromJson(jsonInString, RegisterBadRequestResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
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
            val errorBody = Gson().fromJson(jsonInString, RegisterBadRequestResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }

    fun logoutApi(accessToken: String, signature: String): LiveData<Result<LogoutResponse>> = liveData {
        try {
            val response = ApiConfig.getApiService(signature).logout(accessToken)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.d("login", e.message.toString())
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterBadRequestResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }

    fun predictWithUser(
        multipart: MultipartBody.Part,
        userId: String,
        token: String,
        accessToken: String
    ): LiveData<Result<PredictWithUserResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = ApiConfig.getApiService(token).predictWithUser(userId, multipart, accessToken)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.d("predictWithUser", e.message.toString())
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, LogoutResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }

    fun predictWithoutUser(
        multipart: MultipartBody.Part,
        token: String
    ): LiveData<Result<PredictWithUserResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = ApiConfig.getApiService(token).predictWithoutUser(multipart)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.d("predictWithUser", e.message.toString())
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, PredictWithUserResponse::class.java)
            val errorMessage = errorBody.message
            emit(if (errorMessage != null) {
                Result.Error(errorMessage)
            } else {
                Result.Error("Unknown error occurred") // Or a more specific error message
            })
        }
    }

    fun getHistory(signature: String, userId: String, accessToken: String): LiveData<PagingData<DataItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                HistoryPagingSource(signature, userId, accessToken)
            }
        ).liveData
    }

    companion object{
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPref: UserPreferences,
            historyDatabase: HistoryDatabase
        ): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPref, historyDatabase)
            }.also { instance = it }
        }
    }
}