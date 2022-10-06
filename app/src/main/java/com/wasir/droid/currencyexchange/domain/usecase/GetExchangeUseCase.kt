package com.wasir.droid.currencyexchange.domain.usecase

import com.wasir.droid.currencyexchange.domain.repository.ExchangeRateRepo
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExchangeUseCase @Inject constructor(private val exchangeRateRepo: ExchangeRateRepo) {
    suspend fun getConvertedAmount(
        sellAmount: Double,
        base: String,
        symbols: String
    ): Flow<Resource<Double>> {
        return exchangeRateRepo.calculateReceiverAmount(sellAmount, base, symbols)
    }

    suspend fun convertCurrency(
        sellAmount: Double,
        sellCurrency: String,
        receiveCurrency: String
    ): Flow<Resource<String>> {
        return exchangeRateRepo.convertCurrency(
            sellAmount,
            sellCurrency,
            receiveCurrency
        )
    }
}