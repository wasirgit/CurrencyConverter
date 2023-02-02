package com.wasir.droid.currencyexchange.domain.usecase.accounts

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.data.mockrepo.MockDataRepo
import com.wasir.droid.currencyexchange.data.repository.AccountRepoImpl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetAccountUseCasesTest {
    private val TAG = "GetAccountUseCasesTest"
    private lateinit var getAccUseCases: GetAccountUseCases
    private lateinit var accountRepoImpl: AccountRepoImpl
    private lateinit var currencyExchangeRoomMockRepoImpl: MockDataRepo

    @Before
    fun setUp() {
        currencyExchangeRoomMockRepoImpl = MockDataRepo()
        accountRepoImpl = AccountRepoImpl(currencyExchangeRoomMockRepoImpl)
        getAccUseCases = GetAccountUseCases(
            GetAddCurrencyUseCase(accountRepoImpl),
            GetAccountUseCase(accountRepoImpl),
            GetCurrencyListUseCase(accountRepoImpl)
        )
    }

    @Test
    fun `add currency for positive case`(): Unit = runBlocking {
        getAccUseCases.addCurrency("EUR").collect {
            when (it) {
                is Resource.Error -> {
                }
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Truth.assertThat(it.data).isEqualTo("Currency EUR added successfully")
                }
            }
        }
    }

    @Test
    fun `add invalid currency`(): Unit = runBlocking {
        getAccUseCases.addCurrency("EE").collect {
            when (it) {
                is Resource.Error -> {
                    Truth.assertThat(it.message).isEqualTo("Please provide valid currency")
                }
                is Resource.Loading -> {
                }
                is Resource.Success -> {

                }
            }
        }
    }

    @Test
    fun `add duplicate currency`(): Unit = runBlocking {
        getAccUseCases.addCurrency("EUR").collect {
            when (it) {
                is Resource.Error -> {
                    Truth.assertThat(it.message)
                        .isEqualTo("Currency EUR already exist in the system")
                }
                is Resource.Loading -> {
                }
                is Resource.Success -> {

                }
            }
        }
    }

    @Test
    fun `get accounts and check initial balance of all`(): Unit = runBlocking {
        getAccUseCases.addCurrency("EUR").collect()
        getAccUseCases.getAccounts().collect {
            when (it) {
                is Resource.Error -> {}

                is Resource.Loading -> {}

                is Resource.Success -> {
                    assertThat(it.data?.get(0)?.balance).isEqualTo(1000.00)
                }
            }
        }
    }

    @Test
    fun `get currency list`(): Unit = runBlocking {
        getAccUseCases.addCurrency("EUR").collect()
        getAccUseCases.getCurrencies().collect {
            when (it) {
                is Resource.Error -> {}

                is Resource.Loading -> {}

                is Resource.Success -> {
                    assertThat(it.data?.get(0)).isEqualTo("EUR")
                }
            }

        }
    }


}