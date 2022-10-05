package com.wasir.droid.currencyexchange.di.module

import com.wasir.droid.currencyexchange.data.api.CurrencyExchangeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): CurrencyExchangeService {
        return retrofit.create(CurrencyExchangeService::class.java)
    }
}