package com.wasir.droid.currencyexchange.data.repository

import android.util.Log
import com.wasir.droid.currencyexchange.data.api.CurrencyExchangeService
import com.wasir.droid.currencyexchange.data.database.dao.CurrencyExchangeDao
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.database.entity.CurrencyRateEntity
import com.wasir.droid.currencyexchange.domain.repository.ConfigurationRepo
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConfigurationRepoImpl @Inject constructor(
    private val dao: CurrencyExchangeDao,
    private val service: CurrencyExchangeService,
) :
    ConfigurationRepo {
    private val TAG = "ConfigurationRepoImpl"
    override suspend fun loadConfiguration(): Flow<Resource<ConfigEntity>> = flow {
        emit(Resource.Success(dao.getConfig()))
    }

    override suspend fun getRateByBaseSymbol(base: String):Flow<Resource<CurrencyRateEntity>> = flow {
        emit(Resource.Loading())
        try {
            val exchangeRate = service.getRateByBaseSymbol(base)
            val rateResponseEntity = exchangeRate.toCurrencyRateEntity()
            Log.d(TAG, "rateResponseEntity: $rateResponseEntity")
            rateResponseEntity?.let { data ->
                dao.insertCurrencyRate(data)
            }

            Log.d(TAG, "from DB: ${dao.getCurrencyEntity()}")
            emit(Resource.Success(rateResponseEntity))
        } catch (e: Exception) {
            Log.e(TAG, "getRateByBaseSymbol: $e")
            emit(Resource.Error(e.message))
        }

    }

}