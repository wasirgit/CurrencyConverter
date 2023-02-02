package com.wasir.droid.currencyexchange.domain.repository

import com.wasir.droid.currencyexchange.common.Resource
import kotlinx.coroutines.flow.Flow

interface ExchangeRateRepo {
    suspend fun calculateReceiverAmount(
        sellAmount: Double,
        base: String,
        symbols: String
    ): Flow<Resource<Double>>

    suspend fun convertCurrency(
        sellAmount: Double,
        sellCurrency: String,
        receiveCurrency: String
    ): Flow<Resource<String>>

}