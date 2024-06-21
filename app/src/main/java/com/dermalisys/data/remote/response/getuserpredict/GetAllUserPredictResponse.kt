package com.dermalisys.data.remote.response.getuserpredict

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetAllUserPredictResponse(

	@field:SerializedName("data")
	val data: List<DataItem> = emptyList(),

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String
)

@Parcelize
data class TreatmentItem(

	@field:SerializedName("merk")
	val merk: List<String>,

	@field:SerializedName("tipe")
	val tipe: String,

	@field:SerializedName("zatAktif")
	val zatAktif: String
): Parcelable

@Parcelize
data class Cause(

	@field:SerializedName("section1")
	val section1: String? = null,

	@field:SerializedName("section2")
	val section2: List<String>
): Parcelable

@Entity(tableName = "history")
@Parcelize
data class DataItem(

	@field:SerializedName("treatment")
	val treatment: List<TreatmentItem>,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("cause")
	val cause: Cause,

	@field:SerializedName("label")
	val label: String,

	@field:SerializedName("latinName")
	val latinName: String,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("confidenceScore")
	val confidenceScore: Double,

	@field:SerializedName("symptom")
	val symptom: Symptom,

	@field:SerializedName("skinDiseaseImage")
	val skinDiseaseImage: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("uploadedImage")
	val uploadedImage: String,

	@PrimaryKey
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
): Parcelable

@Parcelize
data class Symptom(

	@field:SerializedName("section1")
	val section1: String? = null,

	@field:SerializedName("section2")
	val section2: List<String>
): Parcelable
