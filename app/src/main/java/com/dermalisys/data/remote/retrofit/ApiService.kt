package com.dermalisys.data.remote.retrofit

import com.dermalisys.data.remote.response.login.LoginBadRequestResponse
import com.dermalisys.data.remote.response.login.LoginOkResponse
import com.dermalisys.data.remote.response.register.RegisterOkResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
}