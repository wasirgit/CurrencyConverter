package com.wasir.droid.currencyexchange.presentation.convert.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wasir.droid.currencyexchange.domain.usecase.settings.SettingsUseCase
import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.wasir.android.dev.data.networking.CoroutineDispatcherProvider
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val accountUseCases: GetAccountUseCases,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {
    private val _addCurrencyStateFlow = MutableSharedFlow<Resource<String>>()
    val addCurrencyStateFlow = _addCurrencyStateFlow.asSharedFlow()

    private val _updateCommissionStateFlow = MutableSharedFlow<Resource<Boolean>>()
    val updateCommissionStateFlow = _updateCommissionStateFlow.asSharedFlow()

    fun addCurrency(currency: String) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            accountUseCases.addCurrency(currency)
                .onEach { result ->
                    _addCurrencyStateFlow.emit(result)
                }.launchIn(this)
        }
    }

    fun updateCommission(updatedCommission: Double) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            settingsUseCase.updateCommission(updatedCommission)
                .onEach { result ->
                    _updateCommissionStateFlow.emit(result)
                }.launchIn(this)
        }
    }

    fun updateSyncTime(updatedTimeInSec: Int) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            settingsUseCase.updateSyncTime(updatedTimeInSec)
                .launchIn(this)
        }
    }

    fun updateFreeConversionPosition(freeConversionPosition: Int) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            settingsUseCase.updateFreeConversionPosition(freeConversionPosition)
                .launchIn(this)
        }
    }

    fun updateMaxFreeAmount(maxFreeAmount: Double) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            settingsUseCase.updateMaxFreeAmount(maxFreeAmount)
                .launchIn(this)
        }
    }

    fun updateNumberOfFreeConversion(totalNumber: Int) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            settingsUseCase.updateNumberOfFreeConversion(totalNumber)
                .launchIn(this)
        }
    }
}