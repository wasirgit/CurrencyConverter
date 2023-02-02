package com.wasir.droid.currencyexchange.presentation.convert.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.wasir.droid.currencyexchange.common.FormatUtils
import com.wasir.droid.currencyexchange.data.database.entity.AccountEntity
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.mockrepo.MockDataRepo
import com.wasir.droid.currencyexchange.data.repository.AccountRepoImpl
import com.wasir.droid.currencyexchange.data.repository.ExchangeRateRepoImpl
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCase
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAccountUseCases
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetAddCurrencyUseCase
import com.wasir.droid.currencyexchange.domain.usecase.accounts.GetCurrencyListUseCase
import com.wasir.droid.currencyexchange.domain.usecase.exchange.GetConvertCurrencyUseCase
import com.wasir.droid.currencyexchange.domain.usecase.exchange.GetConvertedRateUseCase
import com.wasir.droid.currencyexchange.domain.usecase.exchange.GetExchangeUseCases
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import me.wasir.android.dev.data.networking.CoroutineDispatcherProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrencyExchangeViewModelTest {
    @get:Rule
    var taskInstantExecutorRule = InstantTaskExecutorRule()


    private lateinit var currencyExchangeViewModel: CurrencyExchangeViewModel
    private lateinit var getAccountUseCases: GetAccountUseCases
    private lateinit var getExchangeUseCases: GetExchangeUseCases
    private lateinit var dispatcherProvider: CoroutineDispatcherProvider
    private lateinit var accountRepo: AccountRepoImpl
    private lateinit var exchangeRateRepoImpl: ExchangeRateRepoImpl
    private lateinit var formatUtils: FormatUtils
    private lateinit var currencyExchangeRoomMockRepoImpl: MockDataRepo
    val dispatcher = UnconfinedTestDispatcher()
    private val testDispatcher = StandardTestDispatcher()

    private val testScope = TestScope(testDispatcher)
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        formatUtils = FormatUtils()
        currencyExchangeRoomMockRepoImpl = MockDataRepo()
        exchangeRateRepoImpl = ExchangeRateRepoImpl(currencyExchangeRoomMockRepoImpl, formatUtils)
        accountRepo = AccountRepoImpl(currencyExchangeRoomMockRepoImpl)
        getAccountUseCases = GetAccountUseCases(
            GetAddCurrencyUseCase(accountRepo),
            GetAccountUseCase(accountRepo), GetCurrencyListUseCase(accountRepo)
        )

        getExchangeUseCases = GetExchangeUseCases(
            GetConvertedRateUseCase(exchangeRateRepoImpl),
            GetConvertCurrencyUseCase(exchangeRateRepoImpl)
        )
        dispatcherProvider = CoroutineDispatcherProvider()
        currencyExchangeViewModel =
            CurrencyExchangeViewModel(getAccountUseCases, getExchangeUseCases, dispatcherProvider)
    }

    @Test
    fun `insert currency and check size of accounts`(): Unit = runBlocking {
        addAccount()
        val job = launch {
            currencyExchangeViewModel.accountStateFlow.drop(1).test {
                val emission = awaitItem()
                assertThat(emission.data?.size).isEqualTo(3)
                cancelAndConsumeRemainingEvents()

            }
        }
        currencyExchangeViewModel.getAccounts()
        job.join()
        job.cancel()
    }

    @Test
    fun `insert currency and check currency and balance`(): Unit = runBlocking {
        addAccount()
        val job = launch {
            currencyExchangeViewModel.accountStateFlow.drop(1).test {
                val emission = awaitItem()
                assertThat(emission.data?.get(0)?.currencyCode).isEqualTo("EUR")
                assertThat(emission.data?.get(0)?.balance).isEqualTo(1000.00)
                cancelAndConsumeRemainingEvents()

            }
        }
        currencyExchangeViewModel.getAccounts()
        job.join()
        job.cancel()
    }

    @Test
    fun `get currency list and check size`(): Unit = runBlocking {
        addAccount()
        val job = launch {
            currencyExchangeViewModel.currenciesStateFlow.drop(1).test {
                val emission = awaitItem()
                assertThat(emission.data?.size).isEqualTo(3)
                assertThat(emission.data?.get(0)).isEqualTo("EUR")
                cancelAndConsumeRemainingEvents()

            }
        }
        currencyExchangeViewModel.getCurrencyList()
        job.join()
        job.cancel()
    }

    @Test
    fun `calculate receiver amount`(): Unit = runBlocking {
        addAccount()
        val job = launch {
            currencyExchangeViewModel.receiverAmountStateFlow.drop(1).test {
                val emission = awaitItem()
                assertThat((formatUtils.formatAmountWithOutSign(emission.data!!))).isEqualTo(
                    formatUtils.formatAmountWithOutSign(11.34921)
                )
                cancelAndConsumeRemainingEvents()

            }
        }
        currencyExchangeViewModel.calculateReceiverAmount(10.00, "EUR", "USD")
        job.join()
        job.cancel()
    }

    @Test
    fun `first 2 conversion are free`(): Unit = runBlocking {
        addAccount()
    val config = ConfigEntity(
            id = 1,
            total_free_conversion = 2,
            total_convert = 1,
            max_free_amount = 200.00,
            every_nth_conversion_free = 10,
            commission = 0.7,
            syncTime = 5
        )
        currencyExchangeRoomMockRepoImpl.setConfigEntity(config)
        val job = launch {
            currencyExchangeViewModel.convertStateFlow.drop(1).test {
                val emission = awaitItem()
                print(emission.data)
                assertThat(emission.data).doesNotContain("Commission Fee")
                cancelAndConsumeRemainingEvents()
            }
        }
        currencyExchangeViewModel.convertCurrency(1.0, "EUR", "USD")
        job.join()
        job.cancel()
    }

    @Test
    fun `charge applied on 3rd conversion `() = runBlocking{
        addAccount()
        val config = ConfigEntity(
            id = 1,
            total_free_conversion = 2,
            total_convert = 2,
            max_free_amount = 200.00,
            every_nth_conversion_free = 10,
            commission = 0.7,
            syncTime = 5
        )
        currencyExchangeRoomMockRepoImpl.setConfigEntity(config)
        val job = launch {
            currencyExchangeViewModel.convertStateFlow.drop(1).test {
                val emission = awaitItem()
                print(emission.data)
                assertThat(emission.data).contains("Commission Fee")
                cancelAndConsumeRemainingEvents()
            }
        }
        currencyExchangeViewModel.convertCurrency(1.0, "EUR", "USD")
        job.join()
        job.cancel()
    }


    @Test
    fun `apply charge for more than max amount conversion`() = runBlocking {
        addAccount()
        val config = ConfigEntity(
            id = 1,
            total_free_conversion = 2,
            total_convert = 0,
            max_free_amount = 200.00,
            every_nth_conversion_free = 10,
            commission = 0.7,
            syncTime = 5
        )
        currencyExchangeRoomMockRepoImpl.setConfigEntity(config)
        val job = launch {
            currencyExchangeViewModel.convertStateFlow.drop(1).test {
                val emission = awaitItem()
                print(emission.data)
                assertThat(emission.data).contains("Commission Fee")
                cancelAndConsumeRemainingEvents()
            }
        }
        currencyExchangeViewModel.convertCurrency(250.0, "EUR", "USD")
        job.join()
        job.cancel()
    }

    @Test
    fun `every 3rd conversion is free`() = runBlocking {
        addAccount()
        val config = ConfigEntity(
            id = 1,
            total_free_conversion = 2,
            total_convert = 2,
            max_free_amount = 200.00,
            every_nth_conversion_free = 3,
            commission = 0.7,
            syncTime = 5
        )
        currencyExchangeRoomMockRepoImpl.setConfigEntity(config)
        val job = launch {
            currencyExchangeViewModel.convertStateFlow.drop(1).test {
                val emission = awaitItem()
                print(emission.data)
                assertThat(emission.data).doesNotContain("Commission Fee")
                cancelAndConsumeRemainingEvents()
            }
        }
        currencyExchangeViewModel.convertCurrency(2.0, "EUR", "USD")
        job.join()
        job.cancel()
    }


    private fun addAccount() = runBlocking {
        currencyExchangeRoomMockRepoImpl.insertAccount(AccountEntity("EUR", 1000.00))
        currencyExchangeRoomMockRepoImpl.insertAccount(AccountEntity("USD", 1000.00))
        currencyExchangeRoomMockRepoImpl.insertAccount(AccountEntity("BGN", 1000.00))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}