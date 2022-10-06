package com.wasir.droid.currencyexchange.domain.repository

import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SettingsRepo {
    suspend fun addCurrency(currency: String): Flow<Resource<String>>

    suspend fun updateCommission(updatedCommission: Double): Flow<Resource<Boolean>>
    suspend fun updateSyncTime(updatedTimeInSec: Int): Flow<Resource<Boolean>>
    suspend fun updateFreeConversionPosition(freeConversionPosition: Int): Flow<Resource<Boolean>>
    suspend fun updateMaxFreeAmount(maxFreeAmount: Double): Flow<Resource<Boolean>>
    suspend fun  updateNumberOfFreeConversion(totalNumber: Int):Flow<Resource<Boolean>>
}