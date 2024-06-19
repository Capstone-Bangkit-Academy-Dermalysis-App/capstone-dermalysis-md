package com.dermalisys.data.remote.response.predict

import com.google.gson.annotations.SerializedName

data class PredictWithUserResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class Data(

	@field:SerializedName("treatment")
	val treatment: List<TreatmentItem>,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("cause")
	val cause: Cause,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("latinName")
	val latinName: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("confidenceScore")
	val confidenceScore: Any? = null,

	@field:SerializedName("symptom")
	val symptom: Symptom,

	@field:SerializedName("skinDiseaseImage")
	val skinDiseaseImage: String? = null,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("uploadedImage")
	val uploadedImage: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class Cause(

	@field:SerializedName("section1")
	val section1: String,

	@field:SerializedName("section2")
	val section2: List<String>
)

data class TreatmentItem(

	@field:SerializedName("merk")
	val merk: List<String>,

	@field:SerializedName("tipe")
	val tipe: String? = null,

	@field:SerializedName("zatAktif")
	val zatAktif: String? = null
)

data class Symptom(

	@field:SerializedName("section1")
	val section1: String? = null,

	@field:SerializedName("section2")
	val section2: List<String>
)
