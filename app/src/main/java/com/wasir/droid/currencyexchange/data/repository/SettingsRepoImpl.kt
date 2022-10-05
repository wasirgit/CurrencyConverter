package com.wasir.droid.currencyexchange.data.repository

import com.wasir.droid.currencyexchange.data.database.dao.CurrencyExchangeDao
import com.wasir.droid.currencyexchange.data.database.entity.AccountEntity
import com.wasir.droid.currencyexchange.domain.repository.SettingsRepo
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SettingsRepoImpl @Inject constructor(private val dao: CurrencyExchangeDao) : SettingsRepo {
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
                val result: Int = dao.updateCommission(updatedCommission)
                if (result == 1) {
                    emit(Resource.Success(true))
                } else {
                    emit(Resource.Error("Commission update failed"))
                }

            } catch (e: Exception) {
                emit(Resource.Error("Commission update failed"))
            }

        }

    override suspend fun updateSyncTimeCommission(updatedTimeInSec: Int): Flow<Resource<Boolean>> =
        flow {
            try {
                val result: Int = dao.updateSyncTime(updatedTimeInSec)
                if (result == 1) {
                    emit(Resource.Success(true))
                } else {
                    emit(Resource.Error("Commission update failed"))
                }

            } catch (e: Exception) {
                emit(Resource.Error("Commission update failed"))
            }

        }
}