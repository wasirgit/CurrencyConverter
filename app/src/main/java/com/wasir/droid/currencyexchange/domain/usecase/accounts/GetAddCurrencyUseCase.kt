package com.wasir.droid.currencyexchange.domain.usecase.accounts

import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.domain.repository.AccountRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAddCurrencyUseCase @Inject constructor(private val accountRepo: AccountRepo) {
    suspend operator fun invoke(currency: String): Flow<Resource<String>> {
        return accountRepo.addCurrency(currency)
    }
}