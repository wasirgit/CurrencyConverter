package com.wasir.droid.currencyexchange.presentation.convert.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.data.model.CurrencyRateResponse
import com.wasir.droid.currencyexchange.domain.usecase.GetAccountUseCase
import com.wasir.droid.currencyexchange.domain.usecase.GetExchangeUseCase
import com.wasir.droid.currencyexchange.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.wasir.android.dev.data.networking.CoroutineDispatcherProvider
import javax.inject.Inject

@HiltViewModel
class CurrencyExchangeViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val getExchangeUseCase: GetExchangeUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {
    private val TAG = "CEViewModel"
    private var rateByCurrencyJob: Job? = null
    private var calculateReceiverAmountJob: Job? = null
    private val _accountStateFlow = MutableStateFlow<Resource<List<Account>>>(Resource.Loading())
    val accountStateFlow = _accountStateFlow.asStateFlow()
    private val _currenciesStateFlow = MutableStateFlow<Resource<List<String>>>(Resource.Loading())
    val currenciesStateFlow = _currenciesStateFlow.asStateFlow()

    private val _rateStateFlow =
        MutableStateFlow<Resource<CurrencyRateResponse>>(Resource.Loading())
    val rateStateFlow = _rateStateFlow.asStateFlow()
    private val _receiverAmountStateFlow = MutableStateFlow<Resource<Double>>(Resource.Loading())
    val receiverAmountStateFlow = _receiverAmountStateFlow.asStateFlow()

    private val _convertStateFlow = MutableStateFlow<Resource<String>>(Resource.Loading())
    val convertStateFlow = _convertStateFlow.asStateFlow()

    fun loadConfiguration() {
        viewModelScope.launch(dispatcherProvider.IO()) {
            getAccountUseCase.getAccounts().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _accountStateFlow.value = Resource.Success(result.data)
                        Log.d(TAG, "Success: ${result.data}")
                    }
                    is Resource.Error -> {
                        _accountStateFlow.value = Resource.Error(result.message)
                        Log.d(TAG, "ERROR: ${result.message}")
                    }
                    is Resource.Loading -> {
                        _accountStateFlow.value = Resource.Loading(null)
                        Log.d(TAG, "Loading: ${result.message} ${result.data}")
                    }
                }

            }.launchIn(this)
        }
    }

    fun getAccounts() {
        viewModelScope.launch(dispatcherProvider.IO()) {
            getAccountUseCase.getAccounts().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _accountStateFlow.value = Resource.Success(result.data)
                        Log.d(TAG, "Success: ${result.data}")
                    }
                    is Resource.Error -> {
                        _accountStateFlow.value = Resource.Error(result.message)
                        Log.d(TAG, "ERROR: ${result.message}")
                    }
                    is Resource.Loading -> {
                        _accountStateFlow.value = Resource.Loading(null)
                        Log.d(TAG, "Loading: ${result.message} ${result.data}")
                    }
                }

            }.launchIn(this)
        }
    }

    fun getCurrencyList() {
        viewModelScope.launch(dispatcherProvider.IO()) {
            getAccountUseCase.getCurrencyList().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _currenciesStateFlow.value = Resource.Success(result.data)
                    }
                    is Resource.Error -> {
                        _currenciesStateFlow.value = Resource.Error(result.message)
                    }
                    is Resource.Loading -> {
                        _currenciesStateFlow.value = Resource.Loading(null)
                    }
                }
            }.launchIn(this)
        }
    }

    fun getRateByBaseSymbol(base: String, symbol: String) {
        rateByCurrencyJob?.cancel()
        rateByCurrencyJob = viewModelScope.launch(dispatcherProvider.IO()) {
            getExchangeUseCase.getRateByBaseSymbol(base, symbol)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _rateStateFlow.value = Resource.Success(result.data)
                        }
                        is Resource.Error -> {
                            _rateStateFlow.value = Resource.Error(result.message)
                        }
                        is Resource.Loading -> {
                            _rateStateFlow.value = Resource.Loading(null)
                        }
                    }
                }.launchIn(this)
        }
    }

    fun calculateReceiverAmount(sellAmount: Double, exchangeRate: Double) {
        calculateReceiverAmountJob?.cancel()
        calculateReceiverAmountJob = viewModelScope.launch(dispatcherProvider.IO()) {
            getExchangeUseCase.getConvertedAmount(sellAmount, exchangeRate).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _receiverAmountStateFlow.value = Resource.Success(result.data)
                    }
                    is Resource.Error -> {
                        _receiverAmountStateFlow.value = Resource.Error(result.message)
                    }
                    is Resource.Loading -> {
                        _receiverAmountStateFlow.value = Resource.Loading(null)
                    }
                }
            }.launchIn(this)
        }
    }

    fun convertCurrency(
        sellAmount: Double,
        sellCurrency: String,
        receiveCurrency: String,
        exchangeRate: Double
    ) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            getExchangeUseCase.convertCurrency(
                sellAmount,
                sellCurrency,
                receiveCurrency,
                exchangeRate
            )
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _convertStateFlow.value = Resource.Success(result.data)
                        }
                        is Resource.Error -> {
                            _convertStateFlow.value = Resource.Error(result.message)
                        }
                        is Resource.Loading -> {
                            _convertStateFlow.value = Resource.Loading(null)
                        }
                    }
                }.launchIn(this)
        }
    }
}