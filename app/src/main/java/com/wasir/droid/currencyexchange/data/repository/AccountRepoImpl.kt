package com.wasir.droid.currencyexchange.data.repository

import android.util.Log
import com.wasir.droid.currencyexchange.data.database.dao.CurrencyExchangeDao
import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.data.networking.exception.ApiError
import com.wasir.droid.currencyexchange.domain.repository.AccountRepo
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AccountRepoImpl @Inject constructor(
    private val dao: CurrencyExchangeDao,
    private val apiError: ApiError
) : AccountRepo {
    private val TAG = "AccountRepoImpl"
    override suspend fun getAccount(): Flow<Resource<List<Account>>> = flow {
        emit(Resource.Loading())
        try {
            val accounts = dao.getAccountList()
            emit(Resource.Success(accounts.map { it.toAccount() }))
        } catch (e: Exception) {
            Log.e(TAG, "getAccount: ${e.toString()}")
            emit(Resource.Error(apiError.auditError(e)))
        }
    }

    override suspend fun getCurrencyList(): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading())
        try {
            val currencyList = dao.getCurrencyList()
            emit(Resource.Success(currencyList))
        } catch (e: Exception) {
            Log.e(TAG, "getAccount: ${e.toString()}")
            emit(Resource.Error(apiError.auditError(e)))
        }

    }
}