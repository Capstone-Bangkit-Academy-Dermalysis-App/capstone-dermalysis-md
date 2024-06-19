package com.dermalisys.data.remote.response.updateuserdisplay

import com.google.gson.annotations.SerializedName

data class UpdateUserDisplayNameResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
