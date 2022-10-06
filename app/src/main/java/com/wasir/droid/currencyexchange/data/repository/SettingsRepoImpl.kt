package com.wasir.droid.currencyexchange.data.repository

import com.wasir.droid.currencyexchange.data.database.dao.CurrencyExchangeDao
import com.wasir.droid.currencyexchange.data.database.entity.AccountEntity
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.scheduler.AppConfigSync
import com.wasir.droid.currencyexchange.domain.repository.SettingsRepo
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SettingsRepoImpl @Inject constructor(
    private val dao: CurrencyExchangeDao,
    private val scheduler: AppConfigSync
) :
    SettingsRepo {
    private val TAG = "SettingsRepoImpl"
    override suspend fun addCurrency(currency: String): Flow<Resource<String>> = flow {
        val isExist: Boolean = dao.isCurrencyExists(currency)
        if (isExist) {
            emit(Resource.Error("Currency $currency already exist in the system"))
        } else {
            val accountEntity = AccountEntity(currencyCode = currency, 1000.00.toDouble())
            dao.insertAccount(accountEntity)
            emit(Resource.Error("Currency $currency added successfully"))
        }
    }


    override suspend fun updateCommission(updatedCommission: Double): Flow<Resource<Boolean>> =
        flow {
            try {
                dao.updateCommission(updatedCommission)
                val config: ConfigEntity = dao.getConfig()
                scheduler.updateConfig(config)
            } catch (e: Exception) {
                emit(Resource.Error("Commission update failed"))
            }

        }

    override suspend fun updateSyncTime(updatedTimeInSec: Int): Flow<Resource<Boolean>> =
        flow {
            try {
                dao.updateSyncTime(updatedTimeInSec)
                val config: ConfigEntity = dao.getConfig()
                scheduler.updateConfig(config)
            } catch (e: Exception) {
                emit(Resource.Error("Sync time update failed"))
            }

        }

    override suspend fun updateFreeConversionPosition(freeConversionPosition: Int): Flow<Resource<Boolean>> =
        flow {
            try {
                dao.updateFreeConversionPosition(freeConversionPosition)
                val config: ConfigEntity = dao.getConfig()
                scheduler.updateConfig(config)
            } catch (e: Exception) {
                emit(Resource.Error("Free conversion position update failed"))
            }
        }

    override suspend fun updateMaxFreeAmount(maxFreeAmount: Double): Flow<Resource<Boolean>> =
        flow {
            try {
                dao.updateMaxFreeAmount(maxFreeAmount)
                val config: ConfigEntity = dao.getConfig()
                scheduler.updateConfig(config)
            } catch (e: Exception) {
                emit(Resource.Error("Max free amount update failed"))
            }
        }

    override suspend fun updateNumberOfFreeConversion(totalNumber: Int): Flow<Resource<Boolean>> =
        flow {
            try {
                dao.updateNumberOfFreeConversion(totalNumber)
                val config: ConfigEntity = dao.getConfig()
                scheduler.updateConfig(config)
            } catch (e: Exception) {
                emit(Resource.Error("Max free amount update failed"))
            }
        }

}