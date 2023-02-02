package com.wasir.droid.currencyexchange.domain.usecase.exchange


import com.google.common.truth.Truth.assertThat
import com.wasir.droid.currencyexchange.common.FormatUtils
import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.data.mockrepo.MockDataRepo
import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.data.repository.AccountRepoImpl
import com.wasir.droid.currencyexchange.data.repository.ExchangeRateRepoImpl
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCase
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAddCurrencyUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class GetConvertCurrencyUseCaseTest {
    private lateinit var convertCurrency: GetConvertCurrencyUseCase
    private lateinit var exchangeRateRepoImpl: ExchangeRateRepoImpl
    private lateinit var formatUtils: FormatUtils
    private lateinit var accountRepoImpl: AccountRepoImpl
    private lateinit var addCurrency: GetAddCurrencyUseCase
    private lateinit var getAccounts: GetAccountUseCase
    private lateinit var currencyExchangeRoomMockRepoImpl: MockDataRepo

    @Before
    fun setup() {
        currencyExchangeRoomMockRepoImpl = MockDataRepo()
        formatUtils = FormatUtils()
        exchangeRateRepoImpl =
            ExchangeRateRepoImpl(currencyExchangeRoomMockRepoImpl, formatUtils)
        accountRepoImpl = AccountRepoImpl(currencyExchangeRoomMockRepoImpl)
        convertCurrency = GetConvertCurrencyUseCase(exchangeRateRepoImpl)
        addCurrency = GetAddCurrencyUseCase(accountRepoImpl)
        getAccounts = GetAccountUseCase(accountRepoImpl)
    }



    @Test
    fun `first 2 conversion are free`(): Unit = runBlocking {
        addAccount()
        convertCurrency(1.0, "EUR", "USD").collect {
            when (it) {
                is Resource.Error -> {
                    fail(it.message)
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    assertThat(it.data).doesNotContain("Commission Fee")
                }
            }
        }
        convertCurrency(1.0, "EUR", "USD").collect {
            when (it) {
                is Resource.Error -> {
                    fail(it.message)
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    assertThat(it.data).doesNotContain("Commission Fee")
                }
            }
        }
    }


    @Test
    fun `charge applied on 3rd conversion `() = runBlocking {
        addAccount()
        convertCurrency(1.0, "EUR", "USD").collect {
            when (it) {
                is Resource.Error -> {
                    fail(it.message)
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    assertThat(it.data).doesNotContain("Commission Fee")
                }
            }
        }
        convertCurrency(1.0, "EUR", "USD").collect {
            when (it) {
                is Resource.Error -> {
                    fail(it.message)
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    assertThat(it.data).doesNotContain("Commission Fee")
                }
            }
        }
        convertCurrency(1.0, "EUR", "USD").collect {
            when (it) {
                is Resource.Error -> {
                    fail(it.message)
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    assertThat(it.data).contains("Commission Fee - ")
                }
            }
        }
    }

    @Test
    fun `apply charge for more than max amount conversion`() = runBlocking {
        addAccount()
        convertCurrency(201.0, "EUR", "USD").collect {
            when (it) {
                is Resource.Error -> {
                    fail(it.message)
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    assertThat(it.data).contains("Commission Fee")
                }
            }
        }
    }

    @Test
    fun `every 4rd conversion is free`() = runBlocking {
        addAccount()
        for (i in 1..8) {
            convertCurrency(2.0, "EUR", "USD").collect {
                when (it) {
                    is Resource.Error -> {
                        fail(it.message)
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (i % 4 == 0) {
                            assertThat(it.data).doesNotContain("Commission Fee")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `commission are deducted from both account`() = runBlocking {
        addAccount()
        val config = currencyExchangeRoomMockRepoImpl.getConfig()

        val base = "EUR"
        val symbol = "USD"
        val sellAmount = 201.00
        val convertedAmount = sellAmount * 1.134921  //228.119121
        val sellCommission = (config.commission * sellAmount) / 100 //=1.407
        val receiveCommission = (config.commission * convertedAmount) / 100 //= 1.596833847
        convertCurrency(sellAmount, base, symbol).collect {
            when (it) {
                is Resource.Error -> {
                    fail(it.message)
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    getAccounts().collect {
                        val accounts: List<Account>? = it.data
                        if (accounts != null) {
                            for (account in accounts) {
                                if (account.currencyCode.equals(base)) {
                                    assertThat(account.balance).isEqualTo(1000 - (sellAmount + sellCommission))
                                } else if (account.currencyCode.equals(symbol)) {
                                    assertThat(account.balance).isEqualTo(1000 + (convertedAmount - receiveCommission))
                                }
                            }
                        } else {
                            fail(it.data.toString())
                        }
                    }


                }
            }
        }
    }

    @Test
    fun `balance can not be negative after conversion`() = runBlocking {
        addAccount()

        convertCurrency(1001.0, "EUR", "USD").collect {
            when (it) {
                is Resource.Error -> {
                    assertThat(it.message).contains("Balance can't fall below zero")
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    fail(it.data)
                }
            }
        }
    }

    private fun addAccount() = runBlocking {
        addCurrency("EUR").collect()
        addCurrency("USD").collect()
        addCurrency("BGN").collect()
    }
    @After
    fun cleanUp() {
        currencyExchangeRoomMockRepoImpl.cleanUp()
    }

}