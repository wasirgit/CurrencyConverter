package com.wasir.droid.currencyexchange.di.module

import com.wasir.droid.currencyexchange.data.repository.ConfigurationRepoImpl
import com.wasir.droid.currencyexchange.data.repository.SettingsRepoImpl
import com.wasir.droid.currencyexchange.domain.repository.ConfigurationRepo
import com.wasir.droid.currencyexchange.domain.repository.SettingsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {
    @Binds
    abstract fun bindSettingsRepo(settingsRepoImpl: SettingsRepoImpl): SettingsRepo

    @Binds
    abstract fun bindConfigurationRepo(configurationRepoImpl: ConfigurationRepoImpl): ConfigurationRepo
}