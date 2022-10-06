package com.wasir.droid.currencyexchange.domain.repository

import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.database.entity.CurrencyRateEntity
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow


interface ConfigurationRepo {
    suspend fun loadConfiguration(): Flow<Resource<ConfigEntity>>
    suspend fun getRateByBaseSymbol(base: String): Flow<Resource<CurrencyRateEntity>>
}