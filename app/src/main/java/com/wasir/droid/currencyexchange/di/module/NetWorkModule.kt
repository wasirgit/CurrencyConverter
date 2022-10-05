package com.wasir.droid.currencyexchange.di.module

import android.content.Context
import com.google.gson.GsonBuilder
import com.wasir.droid.currencyexchange.BuildConfig
import com.wasir.droid.currencyexchange.data.deserializer.RateApiDeserializer
import com.wasir.droid.currencyexchange.data.model.CurrencyRateResponse
import com.wasir.droid.currencyexchange.data.networking.exception.ApiError
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.wasir.android.dev.data.networking.CoroutineDispatcherProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetWorkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun createGsonConverter(): Converter.Factory {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(CurrencyRateResponse::class.java, RateApiDeserializer())
        val gson = gsonBuilder.create()
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, custonGsonFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(custonGsonFactory)
            .build()

    }

    @Provides
    @Singleton
    fun provideCoroutineDispatcher() = CoroutineDispatcherProvider()

    @Provides
    @Singleton
    fun provideApiErrorHandler(@ApplicationContext context: Context): ApiError {
        return ApiError(context)
    }
}