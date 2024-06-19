package com.dermalisys.data.remote.retrofit

import com.dermalisys.data.remote.response.getuserpredict.DataItem
import com.dermalisys.data.remote.response.getuserpredict.GetAllUserPredictResponse
import com.dermalisys.data.remote.response.login.LoginOkResponse
import com.dermalisys.data.remote.response.logout.LogoutResponse
import com.dermalisys.data.remote.response.predict.PredictWithUserResponse
import com.dermalisys.data.remote.response.register.RegisterOkResponse
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("api/register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): RegisterOkResponse

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginOkResponse

    @FormUrlEncoded
    @POST("api/logout")
    suspend fun logout(
        @Header("Cookie") authToken: String
    ): LogoutResponse

    @Multipart
    @POST("api/predict/{userId}")
    suspend fun predictWithUser(
        @Path("userId") userId: String,
        @Part file: MultipartBody.Part,
        @Header("Cookie") authToken: String
    ): PredictWithUserResponse

    @Multipart
    @POST("api/predict")
    suspend fun predictWithoutUser(
        @Part file: MultipartBody.Part
    ): PredictWithUserResponse

    @GET("/api/predictions/{userId}")
    suspend fun getAllUserHistory(
        @Path("userId") userId: String,
        @Header("Cookie") authToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetAllUserPredictResponse

    @GET("/api/predictions/{userId}")
    suspend fun getAccessTokenWithHistoryAPI(
        @Path("userId") userId: String,
        @Header("Cookie") authToken: String,
    ): GetAllUserPredictResponse
}