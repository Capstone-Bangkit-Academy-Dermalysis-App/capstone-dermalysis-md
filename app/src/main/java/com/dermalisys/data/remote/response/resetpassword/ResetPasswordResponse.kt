package com.dermalisys.data.remote.response.resetpassword

import com.google.gson.annotations.SerializedName

data class ResetPasswordResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
