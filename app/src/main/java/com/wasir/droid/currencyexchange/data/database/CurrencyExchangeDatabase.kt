package com.wasir.droid.currencyexchange.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wasir.droid.currencyexchange.data.database.dao.CurrencyExchangeDao
import com.wasir.droid.currencyexchange.data.database.entity.AccountEntity
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.database.entity.CurrencyRateEntity
import com.wasir.droid.currencyexchange.data.database.typeconverter.RoomTypeConverters

@Database(
    entities = [(AccountEntity::class), (ConfigEntity::class), (CurrencyRateEntity::class)],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class CurrencyExchangeDatabase : RoomDatabase() {
    abstract fun getCurrencyExchangeDao(): CurrencyExchangeDao
}