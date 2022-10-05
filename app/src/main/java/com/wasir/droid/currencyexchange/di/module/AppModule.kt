package com.wasir.droid.currencyexchange.di.module

import android.content.Context
import android.text.InputFilter
import com.wasir.droid.currencyexchange.App
import com.wasir.droid.currencyexchange.utils.AppConfig
import com.wasir.droid.currencyexchange.utils.DecimalLimiter
import com.wasir.droid.currencyexchange.utils.FormatUtils
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
    fun provideAppConfig(): AppConfig {
        return AppConfig()
    }
    @Provides
    @Singleton
    fun provideFormatUtils(): FormatUtils {
        return FormatUtils()
    }
    @Provides
    @ApplicationContext
    fun provideApplicationContext(  @ApplicationContext context: Context): App {
        return context.applicationContext as App
    }
}