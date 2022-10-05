package com.wasir.droid.currencyexchange.data.database.typeconverter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
object RoomTypeConverters {

    @TypeConverter
    @JvmStatic
    fun stringToMap(value: String): Map<String, Double> {
        return Gson().fromJson(value, object : TypeToken<Map<String, Double>>() {}.type)
    }

    @TypeConverter
    @JvmStatic
    fun mapToString(value: Map<String, Double>): String {
        return Gson().toJson(value)
    }
}