package com.wasir.droid.currencyexchange.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class ConfigEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var free_tier_status: Boolean,
    var free_convert_left: Int,
    var commission: Double,
    var syncTime: Int,
)