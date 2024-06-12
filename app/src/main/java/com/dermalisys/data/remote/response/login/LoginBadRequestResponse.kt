package com.dermalisys.data.remote.response.login

import com.google.gson.annotations.SerializedName

data class LoginBadRequestResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
