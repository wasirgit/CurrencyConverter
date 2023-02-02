package com.wasir.droid.currencyexchange.data.repository

import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.data.database.dao.RoomDatabaseDao
import com.wasir.droid.currencyexchange.data.database.entity.AccountEntity
import com.wasir.droid.currencyexchange.data.database.entity.CurrencyRateEntity
import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.domain.repository.AccountRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AccountRepoImpl @Inject constructor(
    private val dao: RoomDatabaseDao
) : AccountRepo {
    override suspend fun addCurrency(currency: String): Flow<Resource<String>> = flow {
        val isExist: Boolean = dao.isCurrencyExists(currency)
        val currencyList: CurrencyRateEntity? = dao.getCurrencyRate()

        if (isExist) {
            emit(Resource.Error("Currency $currency already exist in the system"))
            return@flow
        }
        if (currencyList?.base?.equals(currencyList) == false && !currencyList.rates.containsKey(
                currency
            )
        ) {
            emit(Resource.Error("Please provide valid currency"))
        } else {
            val accountEntity = AccountEntity(currencyCode = currency, 1000.00.toDouble())
            dao.insertAccount(accountEntity)
            emit(Resource.Success("Currency $currency added successfully"))
        }
    }


    override suspend fun getAccount(): Flow<Resource<List<Account>>> = flow {
        try {
            val accounts = dao.getAccountList()
            emit(Resource.Success(accounts.map { it.toAccount() }))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage))
        }
    }

    override suspend fun getCurrencyList(): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading())
        try {
            val currencyList = dao.getCurrencyList()
            emit(Resource.Success(currencyList))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage))
        }

    }
}