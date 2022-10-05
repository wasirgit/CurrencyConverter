package com.wasir.droid.currencyexchange.domain.usecase

import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.domain.repository.AccountRepo
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val currencyConvertRepo: AccountRepo,
) {
    suspend fun getAccounts(): Flow<Resource<List<Account>>> {
        return currencyConvertRepo.getAccount()
    }

    suspend fun getCurrencyList(): Flow<Resource<List<String>>> {
        return currencyConvertRepo.getCurrencyList()
    }
}