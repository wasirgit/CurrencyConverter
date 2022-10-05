package com.wasir.droid.currencyexchange.domain.usecase

import com.wasir.droid.currencyexchange.domain.repository.SettingsRepo
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsUseCase @Inject constructor(private val settingsRepo: SettingsRepo) {
    suspend fun addCurrency(currency: String): Flow<Resource<String>> {
        return settingsRepo.addCurrency(currency)
    }

    suspend fun updateCommission(updatedCommission: Double): Flow<Resource<Boolean>> {
        return settingsRepo.updateCommission(updatedCommission)
    }
    suspend fun updateSyncTimeCommission(updatedTimeInSec: Int): Flow<Resource<Boolean>> {
        return settingsRepo.updateSyncTimeCommission(updatedTimeInSec)
    }
}