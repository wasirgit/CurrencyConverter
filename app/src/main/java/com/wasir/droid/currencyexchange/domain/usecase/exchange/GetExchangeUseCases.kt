package com.wasir.droid.currencyexchange.domain.usecase.exchange

import javax.inject.Inject

data class GetExchangeUseCases @Inject constructor(
    val getConvertedRate: GetConvertedRateUseCase,
    val convertCurrency: GetConvertCurrencyUseCase

)