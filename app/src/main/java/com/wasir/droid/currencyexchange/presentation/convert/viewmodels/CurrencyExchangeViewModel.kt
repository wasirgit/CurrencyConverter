package com.wasir.droid.currencyexchange.presentation.convert.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCases
import com.wasir.droid.currencyexchange.domain.usecase.exchange.GetExchangeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.wasir.android.dev.data.networking.CoroutineDispatcherProvider
import javax.inject.Inject

@HiltViewModel
class CurrencyExchangeViewModel @Inject constructor(
    private val getAccountUseCases: GetAccountUseCases,
    private val getExchangeUseCases: GetExchangeUseCases,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {
    private val TAG = "CEViewModel"
    var base: String = "EUR"
    var symbols: String = "USD"

    private var calculateReceiverAmountJob: Job? = null

    private val _accountStateFlow = MutableStateFlow<Resource<List<Account>>>(Resource.Loading())
    val accountStateFlow = _accountStateFlow.asStateFlow()

    private val _currenciesStateFlow = MutableStateFlow<Resource<List<String>>>(Resource.Loading())
    val currenciesStateFlow = _currenciesStateFlow.asStateFlow()

    private val _receiverAmountStateFlow = MutableSharedFlow<Resource<Double>>()
    val receiverAmountStateFlow = _receiverAmountStateFlow.asSharedFlow()

    private val _convertStateFlow = MutableSharedFlow<Resource<String>>()
    val convertStateFlow = _convertStateFlow.asSharedFlow()


    fun getAccounts() {
        viewModelScope.launch(dispatcherProvider.IO()) {
            getAccountUseCases.getAccounts().onEach { result ->
                _accountStateFlow.value = result
            }.launchIn(viewModelScope)
        }
    }

    fun getCurrencyList() {
        viewModelScope.launch(dispatcherProvider.IO()) {
            getAccountUseCases.getCurrencies().onEach { result ->
                _currenciesStateFlow.value = result
            }.launchIn(viewModelScope)
        }
    }

    fun calculateReceiverAmount(
        sellAmount: Double,
        base: String,
        symbols: String
    ) {
        calculateReceiverAmountJob?.cancel()
        calculateReceiverAmountJob = viewModelScope.launch(dispatcherProvider.IO()) {
            getExchangeUseCases.getConvertedRate(sellAmount, base, symbols).onEach { result ->
                _receiverAmountStateFlow.emit(result)
            }.launchIn(viewModelScope)
        }
    }

    fun convertCurrency(
        sellAmount: Double,
        sellCurrency: String,
        receiveCurrency: String
    ) {
        viewModelScope.launch(dispatcherProvider.IO()) {
            getExchangeUseCases.convertCurrency(
                sellAmount,
                sellCurrency,
                receiveCurrency
            )
                .onEach { result ->
                    _convertStateFlow.emit(result)
                }.launchIn(this)
        }
    }
}