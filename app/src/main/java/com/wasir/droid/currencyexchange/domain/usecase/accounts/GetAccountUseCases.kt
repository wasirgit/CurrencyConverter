package com.wasir.droid.currencyexchange.domain.usecase.accounts

import javax.inject.Inject

data class GetAccountUseCases @Inject constructor(
    val addCurrency: GetAddCurrencyUseCase,
    val getAccounts: GetAccountUseCase,
    val getCurrencies: GetCurrencyListUseCase
)