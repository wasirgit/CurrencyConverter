package com.wasir.droid.currencyexchange.di.module

import com.wasir.droid.currencyexchange.data.repository.AccountRepoImpl
import com.wasir.droid.currencyexchange.domain.repository.AccountRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class AccountModule {
    @Binds
    abstract fun bindAccountRepo(accountRepoImpl: AccountRepoImpl): AccountRepo
}