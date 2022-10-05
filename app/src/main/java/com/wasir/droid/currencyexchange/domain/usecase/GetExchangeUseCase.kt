package com.wasir.droid.currencyexchange.domain.usecase

import com.wasir.droid.currencyexchange.data.model.CurrencyRateResponse
import com.wasir.droid.currencyexchange.domain.repository.ExchangeRateRepo
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExchangeUseCase @Inject constructor(private val exchangeRateRepo: ExchangeRateRepo) {
    suspend fun getRateByBaseSymbol(
        base: String,
        symbol: String
    ): Flow<Resource<CurrencyRateResponse>> {
        return exchangeRateRepo.getRateByBaseSymbol(base, symbol)
    }

    suspend fun getConvertedAmount(
        sellAmount: Double,
        exchangeRate: Double
    ): Flow<Resource<Double>> {
        return exchangeRateRepo.calculateReceiverAmount(sellAmount, exchangeRate)
    }

    suspend fun convertCurrency(
        sellAmount: Double,
        sellCurrency: String,
        receiveCurrency: String,
        exchangeRate: Double
    ): Flow<Resource<String>> {
        return exchangeRateRepo.convertCurrency(sellAmount, sellCurrency, receiveCurrency,exchangeRate)
    }
}