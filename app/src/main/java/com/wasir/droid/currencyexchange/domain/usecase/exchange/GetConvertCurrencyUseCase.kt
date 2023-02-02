package com.wasir.droid.currencyexchange.domain.usecase.exchange

import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.domain.repository.ExchangeRateRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConvertCurrencyUseCase @Inject constructor(private val exchangeRateRepo: ExchangeRateRepo) {
    suspend operator fun invoke(
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