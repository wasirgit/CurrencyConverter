package com.wasir.droid.currencyexchange.di.module

import com.wasir.droid.currencyexchange.data.repository.ExchangeRateRepoImpl
import com.wasir.droid.currencyexchange.domain.repository.AccountRepo
import com.wasir.droid.currencyexchange.domain.repository.ExchangeRateRepo
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCase
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCases
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class ExchangeRateModule {
    @Binds
    abstract fun bindExchangeRateRepo(exchangeRateRepoImpl: ExchangeRateRepoImpl): ExchangeRateRepo
//    @Provides
//    @Singleton
//    fun provideNoteUseCases( currencyConvertRepo: AccountRepo): GetAccountUseCases {
//        return GetAccountUseCases(
//          getAccounts = GetAccountUseCase(currencyConvertRepo)
//        )
//    }
}