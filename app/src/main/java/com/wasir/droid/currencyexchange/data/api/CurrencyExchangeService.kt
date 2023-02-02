package com.wasir.droid.currencyexchange.data.api

import com.wasir.droid.currencyexchange.data.model.CurrencyRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyExchangeService {
    @GET("latest")
    suspend fun getRateByBaseSymbol(
        @Query("base") base: String
    ): CurrencyRateResponse
}