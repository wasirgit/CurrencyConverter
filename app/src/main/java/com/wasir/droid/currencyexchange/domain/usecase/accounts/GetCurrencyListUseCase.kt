package com.wasir.droid.currencyexchange.domain.usecase.accounts

import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.domain.repository.AccountRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrencyListUseCase @Inject constructor(private val currencyConvertRepo: AccountRepo) {
    suspend operator fun invoke(): Flow<Resource<List<String>>> {
        return currencyConvertRepo.getCurrencyList()
    }
}