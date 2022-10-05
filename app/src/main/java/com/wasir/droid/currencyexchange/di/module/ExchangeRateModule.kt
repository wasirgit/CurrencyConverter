package com.wasir.droid.currencyexchange.di.module

import com.wasir.droid.currencyexchange.data.repository.ExchangeRateRepoImpl
import com.wasir.droid.currencyexchange.domain.repository.ExchangeRateRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ExchangeRateModule {
    @Binds
    abstract fun bindExchangeRateRepo(exchangeRateRepoImpl: ExchangeRateRepoImpl): ExchangeRateRepo
}