package com.wasir.droid.currencyexchange.domain.usecase.exchange

import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.domain.repository.ExchangeRateRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConvertedRateUseCase @Inject constructor(private val exchangeRateRepo: ExchangeRateRepo) {
    suspend operator fun invoke(
        sellAmount: Double,
        base: String,
        symbols: String
    ): Flow<Resource<Double>> {
        return exchangeRateRepo.calculateReceiverAmount(sellAmount, base, symbols)
    }
}