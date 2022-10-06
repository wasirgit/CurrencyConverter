package com.wasir.droid.currencyexchange.presentation.convert.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wasir.droid.currencyexchange.domain.usecase.SettingsUseCase
import com.wasir.droid.currencyexchange.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.wasir.android.dev.data.networking.CoroutineDispatcherProvider
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {
    private val _addCurrencyStateFlow =
        MutableStateFlow<Resource<String>>(Resource.Loading())
    val addCurrencyStateFlow = _addCurrencyStateFlow.asStateFlow()

    private val _updateCommissionStateFlow =
        MutableStateFlow<Resource<Boolean>>(Resource.Loading())
    val updateCommissionStateFlow = _updateCommissionStateFlow.asStateFlow()

    fun addCurrency(currency: String) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            settingsUseCase.addCurrency(currency)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _addCurrencyStateFlow.value = Resource.Success(result.data)
                        }
                        is Resource.Error -> {
                            _addCurrencyStateFlow.value = Resource.Error(result.message)
                        }
                        is Resource.Loading -> {
                            _addCurrencyStateFlow.value = Resource.Loading(null)
                        }
                    }
                }.launchIn(this)
        }
    }

    fun updateCommission(updatedCommission: Double) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            settingsUseCase.updateCommission(updatedCommission)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _updateCommissionStateFlow.value = Resource.Success(result.data)
                        }
                        is Resource.Error -> {
                            _updateCommissionStateFlow.value = Resource.Error(result.message)
                        }
                        is Resource.Loading -> {
                            _updateCommissionStateFlow.value = Resource.Loading(null)
                        }
                    }
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