package com.wasir.droid.currencyexchange.di.module

import android.content.Context
import android.text.InputFilter
import com.wasir.droid.currencyexchange.App
import com.wasir.droid.currencyexchange.data.scheduler.AppConfigSync
import com.wasir.droid.currencyexchange.domain.repository.ConfigurationRepo
import com.wasir.droid.currencyexchange.common.DecimalLimiter
import com.wasir.droid.currencyexchange.common.FormatUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideDecimalLimiter(): DecimalLimiter {
        return DecimalLimiter()
    }

    @Provides
    @Singleton
    fun provideInputFilter(decimalLimiter: DecimalLimiter): Array<InputFilter> {
        return arrayOf<InputFilter>(decimalLimiter)
    }

    @Provides
    @Singleton
    fun provideFormatUtils(): FormatUtils {
        return FormatUtils()
    }

    @Provides
    @ApplicationContext
    fun provideApplicationContext(@ApplicationContext context: Context): App {
        return context.applicationContext as App
    }

    @Provides
    @Singleton
    fun provideAppConfigAndScheduler(configurationRepo: ConfigurationRepo): AppConfigSync {
        return AppConfigSync(configurationRepo)
    }
}