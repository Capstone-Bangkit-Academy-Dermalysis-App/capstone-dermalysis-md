package com.dermalisys.data.remote.response.register

import com.google.gson.annotations.SerializedName

data class RegisterBadRequestResponse(

	@field:SerializedName("success")
	val success: String,

	@field:SerializedName("message")
	val message: String
)
