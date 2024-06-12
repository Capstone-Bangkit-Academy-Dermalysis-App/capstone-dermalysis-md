package com.dermalisys.data.remote.response.register

import com.google.gson.annotations.SerializedName

data class RegisterInternalServerErrorResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
