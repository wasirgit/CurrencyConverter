package com.wasir.droid.currencyexchange.presentation.convert.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wasir.droid.currencyexchange.data.model.Account
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
    var base: String = "Select"
    var symbols: String = "Select"

    private var rateByCurrencyJob: Job? = null
    private var calculateReceiverAmountJob: Job? = null

    private val _accountStateFlow = MutableStateFlow<Resource<List<Account>>>(Resource.Loading())
    val accountStateFlow = _accountStateFlow.asStateFlow()

    private val _currenciesStateFlow = MutableStateFlow<Resource<List<String>>>(Resource.Loading())
    val currenciesStateFlow = _currenciesStateFlow.asStateFlow()

    private val _receiverAmountStateFlow = MutableStateFlow<Resource<Double>>(Resource.Loading())
    val receiverAmountStateFlow = _receiverAmountStateFlow.asStateFlow()

    private val _convertStateFlow = MutableStateFlow<Resource<String>>(Resource.Loading())
    val convertStateFlow = _convertStateFlow.asStateFlow()


    fun getAccounts() {
        viewModelScope.launch(dispatcherProvider.IO()) {
            getAccountUseCase.getAccounts().onEach { result ->
                _accountStateFlow.value = result
            }.launchIn(this)
        }
    }

    fun getCurrencyList() {
        viewModelScope.launch(dispatcherProvider.IO()) {
            getAccountUseCase.getCurrencyList().onEach { result ->
                _currenciesStateFlow.value = result
            }.launchIn(this)
        }
    }

    fun calculateReceiverAmount(
        sellAmount: Double,
        base: String,
        symbols: String
    ) {
        calculateReceiverAmountJob?.cancel()
        calculateReceiverAmountJob = viewModelScope.launch(dispatcherProvider.IO()) {
            getExchangeUseCase.getConvertedAmount(sellAmount, base, symbols).onEach { result ->
                _receiverAmountStateFlow.value = result
            }.launchIn(this)
        }
    }

    fun convertCurrency(
        sellAmount: Double,
        sellCurrency: String,
        receiveCurrency: String
    ) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            getExchangeUseCase.convertCurrency(
                sellAmount,
                sellCurrency,
                receiveCurrency
            )
                .onEach { result ->
                    _convertStateFlow.value = result
                }.launchIn(this)
        }
    }
}