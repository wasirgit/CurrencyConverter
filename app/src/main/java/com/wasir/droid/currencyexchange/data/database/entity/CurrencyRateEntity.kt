package com.wasir.droid.currencyexchange.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverters
import com.wasir.droid.currencyexchange.data.database.typeconverter.RoomTypeConverters


@Entity(tableName = "currency_rate")
@ProvidedTypeConverter
data class CurrencyRateEntity(
    @PrimaryKey
    var base: String,
    @TypeConverters(RoomTypeConverters::class)
    var rates: Map<String, Double>
)
