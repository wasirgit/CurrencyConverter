package com.wasir.droid.currencyexchange.di

import android.content.Context
import androidx.room.Room
import com.wasir.droid.currencyexchange.data.database.CurrencyExchangeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class AppModuleTest {
    @Provides
    @Named("testDatabase")
    fun injectInMemoryRoom(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, CurrencyExchangeDatabase::class.java)
            .allowMainThreadQueries()
            .build()

}