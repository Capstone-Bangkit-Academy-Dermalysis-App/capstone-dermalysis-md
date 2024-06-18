package com.dermalisys.data

import androidx.room.TypeConverter
import com.dermalisys.data.remote.response.getuserpredict.Cause
import com.dermalisys.data.remote.response.getuserpredict.Symptom
import com.dermalisys.data.remote.response.getuserpredict.TreatmentItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromTreatmentItemList(treatmentItems: List<TreatmentItem>): String {
        val gson = Gson()
        return gson.toJson(treatmentItems)
    }

    @TypeConverter
    fun toTreatmentItemList(data: String): List<TreatmentItem> {
        val gson = Gson()
        val type = object : TypeToken<List<TreatmentItem>>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun toCauseItemList(causeItems: Cause): String {
        val gson = Gson()
        return gson.toJson(causeItems)
    }

    @TypeConverter
    fun toCauseItemList(data: String): Cause {
        val gson = Gson()
        val type = object : TypeToken<Cause>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun toSymptomItemList(symptomItems: Symptom): String {
        val gson = Gson()
        return gson.toJson(symptomItems)
    }

    @TypeConverter
    fun toSymptomItemList(data: String): Symptom {
        val gson = Gson()
        val type = object : TypeToken<Symptom>() {}.type
        return gson.fromJson(data, type)
    }
}