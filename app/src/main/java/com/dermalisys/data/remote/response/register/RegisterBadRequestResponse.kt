package com.dermalisys.data.remote.response.register

import com.google.gson.annotations.SerializedName

data class RegisterBadRequestResponse(

	@field:SerializedName("password")
	val success: String,

	@field:SerializedName("email")
	val message: String
)
