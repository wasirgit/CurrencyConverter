package com.wasir.droid.currencyexchange.data.repository

import com.wasir.droid.currencyexchange.data.database.dao.CurrencyExchangeDao
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.domain.repository.ConfigurationRepo
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConfigurationRepoImpl @Inject constructor(private val dao: CurrencyExchangeDao) :
    ConfigurationRepo {
    override suspend fun loadConfiguration(): Flow<Resource<ConfigEntity>> = flow {
        emit(Resource.Success(dao.getConfig()))
    }
}