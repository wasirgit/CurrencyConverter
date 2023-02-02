package com.wasir.droid.currencyexchange.domain.usecase.exchange

import com.google.common.truth.Truth
import com.wasir.droid.currencyexchange.common.FormatUtils
import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.data.mockrepo.MockDataRepo
import com.wasir.droid.currencyexchange.data.repository.ExchangeRateRepoImpl
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetConvertedRateUseCaseTest {
    private lateinit var getConvertedRateUseCase: GetConvertedRateUseCase
    private lateinit var exchangeRateRepoImpl: ExchangeRateRepoImpl
    private lateinit var formatUtils: FormatUtils

    @Before
    fun setup() {
        formatUtils = FormatUtils()
        exchangeRateRepoImpl = ExchangeRateRepoImpl(MockDataRepo(), formatUtils)
        getConvertedRateUseCase = GetConvertedRateUseCase(exchangeRateRepoImpl)
    }

    @Test
    fun `get exchange rate of success where base is EUR and 1 EUR is 1'134921 USD`() = runBlocking {
        getConvertedRateUseCase(10.00, "EUR", "USD").collect {
            when (it) {
                is Resource.Error -> {
                }
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Truth.assertThat((formatUtils.formatAmountWithOutSign(it.data!!))).isEqualTo(
                        formatUtils.formatAmountWithOutSign(11.34921)
                    )
                }
            }
        }
    }

    @Test
    fun `get exchange rate of success where base is USD and 1 EUR is 1'134921 USD`() = runBlocking {
        getConvertedRateUseCase(10.00, "USD", "EUR").collect {
            when (it) {
                is Resource.Error -> {
                }
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Truth.assertThat((formatUtils.formatAmountWithOutSign(it.data!!))).isEqualTo(
                        formatUtils.formatAmountWithOutSign(8.8111859768)
                    )
                }
            }
        }
    }

    //EUR = 1
    // USD =1.134921
    // BGN = 1.956957
    @Test
    fun `get exchange rate of success where base is BGN and 1 EUR is 1'956957 BGN`() = runBlocking {
        getConvertedRateUseCase(10.00, "BGN", "USD").collect {
            when (it) {
                is Resource.Error -> {
                }
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Truth.assertThat((formatUtils.formatAmountWithOutSign(it.data!!))).isEqualTo(
                        formatUtils.formatAmountWithOutSign(5.7994171562)
                    )
                }
            }
        }
    }
}