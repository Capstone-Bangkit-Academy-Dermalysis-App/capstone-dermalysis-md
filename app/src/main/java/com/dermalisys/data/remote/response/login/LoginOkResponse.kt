package com.dermalisys.data.remote.response.login

import com.google.gson.annotations.SerializedName

data class LoginOkResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataItem(

	@field:SerializedName("providerId")
	val providerId: Any? = null,

	@field:SerializedName("_tokenResponse")
	val tokenResponse: TokenResponse? = null,

	@field:SerializedName("operationType")
	val operationType: String? = null,

	@field:SerializedName("user")
	val user: User? = null
)

data class User(

	@field:SerializedName("uid")
	val uid: String? = null,

	@field:SerializedName("emailVerified")
	val emailVerified: Boolean? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isAnonymous")
	val isAnonymous: Boolean? = null,

	@field:SerializedName("stsTokenManager")
	val stsTokenManager: StsTokenManager? = null,

	@field:SerializedName("lastLoginAt")
	val lastLoginAt: String? = null,

	@field:SerializedName("apiKey")
	val apiKey: String? = null,

	@field:SerializedName("providerData")
	val providerData: List<ProviderDataItem?>? = null,

	@field:SerializedName("displayName")
	val displayName: String? = null,

	@field:SerializedName("appName")
	val appName: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class StsTokenManager(

	@field:SerializedName("expirationTime")
	val expirationTime: Long? = null,

	@field:SerializedName("accessToken")
	val accessToken: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null
)

data class TokenResponse(

	@field:SerializedName("expiresIn")
	val expiresIn: String? = null,

	@field:SerializedName("kind")
	val kind: String? = null,

	@field:SerializedName("idToken")
	val idToken: String? = null,

	@field:SerializedName("localId")
	val localId: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null
)

data class ProviderDataItem(

	@field:SerializedName("uid")
	val uid: String? = null,

	@field:SerializedName("photoURL")
	val photoURL: Any? = null,

	@field:SerializedName("phoneNumber")
	val phoneNumber: Any? = null,

	@field:SerializedName("providerId")
	val providerId: String? = null,

	@field:SerializedName("displayName")
	val displayName: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
