package com.dermalisys.data.remote.response.getuserpredict

import com.google.gson.annotations.SerializedName

data class GetAllUserPredictErrorResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String
)
