package com.wasir.droid.currencyexchange.domain.repository

import com.wasir.droid.currencyexchange.data.model.CurrencyRateResponse
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ExchangeRateRepo {
    suspend fun loadConfiguration(): Flow<Resource<String>>
    suspend fun getRateByBaseSymbol(base: String, symbol: String): Flow<Resource<CurrencyRateResponse>>
    suspend fun calculateReceiverAmount(sellAmount: Double, exchangeRate: Double): Flow<Resource<Double>>
    suspend fun convertCurrency(sellAmount: Double, sellCurrency:String,receiveCurrency:String, exchangeRate: Double): Flow<Resource<String>>

}