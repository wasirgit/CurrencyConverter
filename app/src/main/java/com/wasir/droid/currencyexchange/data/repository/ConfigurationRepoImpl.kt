package com.wasir.droid.currencyexchange.data.repository

import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.data.api.CurrencyExchangeService
import com.wasir.droid.currencyexchange.data.database.dao.RoomDatabaseDao
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.database.entity.CurrencyRateEntity
import com.wasir.droid.currencyexchange.domain.repository.ConfigurationRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConfigurationRepoImpl @Inject constructor(
    private val dao: RoomDatabaseDao,
    private val service: CurrencyExchangeService,
) :
    ConfigurationRepo {
    override suspend fun loadConfiguration(): Flow<Resource<ConfigEntity>> = flow {
        emit(Resource.Success(dao.getConfig()))
    }

    override suspend fun getRateByBaseSymbol(base: String): Flow<Resource<CurrencyRateEntity>> =
        flow {
            emit(Resource.Loading())
            try {
                val exchangeRate = service.getRateByBaseSymbol(base)
                val rateResponseEntity = exchangeRate.toCurrencyRateEntity()
                rateResponseEntity?.let { data ->
                    dao.insertCurrencyRate(data)
                }

                emit(Resource.Success(rateResponseEntity))
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }

        }

}