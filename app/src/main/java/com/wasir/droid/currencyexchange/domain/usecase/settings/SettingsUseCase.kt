package com.wasir.droid.currencyexchange.domain.usecase.settings

import com.wasir.droid.currencyexchange.domain.repository.SettingsRepo
import com.wasir.droid.currencyexchange.common.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsUseCase @Inject constructor(private val settingsRepo: SettingsRepo) {


    suspend fun updateCommission(updatedCommission: Double): Flow<Resource<Boolean>> {
        return settingsRepo.updateCommission(updatedCommission)
    }

    suspend fun updateSyncTime(updatedTimeInSec: Int): Flow<Resource<Boolean>> {
        return settingsRepo.updateSyncTime(updatedTimeInSec)
    }

    suspend fun updateFreeConversionPosition(freeConversionPosition: Int): Flow<Resource<Boolean>> {
        return settingsRepo.updateFreeConversionPosition(freeConversionPosition)
    }

    suspend fun updateMaxFreeAmount(maxFreeAmount: Double): Flow<Resource<Boolean>> {
        return settingsRepo.updateMaxFreeAmount(maxFreeAmount)
    }

    suspend fun updateNumberOfFreeConversion(totalNumber: Int): Flow<Resource<Boolean>> {
        return settingsRepo.updateNumberOfFreeConversion(totalNumber)
    }
}