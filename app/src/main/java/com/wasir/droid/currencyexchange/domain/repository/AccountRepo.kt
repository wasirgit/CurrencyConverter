package com.wasir.droid.currencyexchange.domain.repository

import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AccountRepo {
    suspend fun getAccount(): Flow<Resource<List<Account>>>
    suspend fun getCurrencyList(): Flow<Resource<List<String>>>
}