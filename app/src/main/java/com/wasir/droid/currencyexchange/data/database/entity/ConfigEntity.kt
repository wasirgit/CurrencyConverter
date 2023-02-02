package com.wasir.droid.currencyexchange.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class ConfigEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var total_free_conversion: Int,
    var total_convert: Int,
    var max_free_amount: Double,
    var every_nth_conversion_free: Int,
    var commission: Double,
    var syncTime: Int,
)