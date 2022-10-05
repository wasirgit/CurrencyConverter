package com.wasir.droid.currencyexchange.di.module

import android.content.Context
import androidx.room.Room
import com.wasir.droid.currencyexchange.data.database.CurrencyExchangeDatabase
import com.wasir.droid.currencyexchange.data.database.dao.CurrencyExchangeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "currency_exchange_db"


    @Provides
    fun provideMyRoomDatabase(
        @ApplicationContext context: Context
    ): CurrencyExchangeDatabase {
        return Room.databaseBuilder(
            context,
            CurrencyExchangeDatabase::class.java,
            DB_NAME
        )
            .createFromAsset("database/initial_config.db")
            .build()
    }

    @Provides
    fun providePrayDao(currencyExchangeDatabase: CurrencyExchangeDatabase): CurrencyExchangeDao {
        return currencyExchangeDatabase.getCurrencyExchangeDao()
    }
}