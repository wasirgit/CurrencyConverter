package com.wasir.droid.currencyexchange.data.repository

import android.util.Log
import com.wasir.droid.currencyexchange.data.api.CurrencyExchangeService
import com.wasir.droid.currencyexchange.data.database.dao.CurrencyExchangeDao
import com.wasir.droid.currencyexchange.data.model.CurrencyRateResponse
import com.wasir.droid.currencyexchange.data.networking.exception.ApiError
import com.wasir.droid.currencyexchange.domain.repository.ExchangeRateRepo
import com.wasir.droid.currencyexchange.utils.FormatUtils
import com.wasir.droid.currencyexchange.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExchangeRateRepoImpl @Inject constructor(
    private val currencyExchangeService: CurrencyExchangeService,
    private val dao: CurrencyExchangeDao,
    private val apiError: ApiError,
    private val formatUtils: FormatUtils
) :
    ExchangeRateRepo {
    private val TAG = "ExchangeRateRepoImpl"
    override suspend fun loadConfiguration(): Flow<Resource<String>> = flow{

    }

    override suspend fun getRateByBaseSymbol(
        base: String,
        symbol: String
    ): Flow<Resource<CurrencyRateResponse>> = flow {
        emit(Resource.Loading())
        try {
            val exchangeRate = currencyExchangeService.getRateByBaseSymbol(base, symbol)
            val rateResponseEntity = exchangeRate.toCurrencyRateEntity()
            Log.d(TAG, "rateResponseEntity: $rateResponseEntity")
            rateResponseEntity?.let {
                dao.insertCurrencyRate(it)
            }
            Log.d(TAG, "from DB: ${dao.getCurrencyEntity()}")
            emit(Resource.Success(exchangeRate))
        } catch (e: Exception) {
            Log.e(TAG, "getRateByBaseSymbol: $e")
            emit(Resource.Error(apiError.auditError(e)))
        }
    }

    override suspend fun calculateReceiverAmount(
        sellAmount: Double,
        exchangeRate: Double
    ): Flow<Resource<Double>> =
        flow {
            emit(Resource.Loading())
            try {
                emit(Resource.Success(sellAmount * exchangeRate))
            } catch (e: Exception) {
                Log.e(TAG, "calculateReceiverAmount: $e")
                emit(Resource.Error(apiError.auditError(e)))
            }
        }

    override suspend fun convertCurrency(
        sellAmount: Double,
        sellCurrency: String,
        receiveCurrency: String,
        exchangeRate: Double
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        if (sellAmount <= 0) {
            emit(Resource.Error("Amount must be greater then 0"))
        } else {
            try {
                val config = dao.getConfig()
                if (!config.free_tier_status || config.free_convert_left <= 0) {
                    emit(Resource.Error("Free tier is over"))
                } else {

                    val sellAccounts = dao.getAccountByCurrency(sellCurrency)
                    val receiveAccount = dao.getAccountByCurrency(receiveCurrency)
                    val convertedAmount = sellAmount * exchangeRate
                    val commission = getCommission(convertedAmount, config.commission);
                    val accBalanceAfterSell =
                        sellAccounts.balance - (convertedAmount + commission)
                    val accBalanceAfterReceive =
                        receiveAccount.balance + (convertedAmount - commission)
                    Log.d(
                        TAG,
                        "before sell: ${sellAccounts.balance} afterSellAmount: $accBalanceAfterSell"
                    )
                    if (accBalanceAfterSell < 0) {
                        emit(Resource.Error("Balance can't fall below zero"))
                    } else {
                        dao.updateAccBalanceByCurrencyCode(sellCurrency, accBalanceAfterSell)
                        dao.updateAccBalanceByCurrencyCode(receiveCurrency, accBalanceAfterReceive)
                        val message =
                            "You have converted ${formatUtils.formatAmountWithOutSign(sellAmount)} $sellCurrency to ${
                                formatUtils.formatAmountWithOutSign(
                                    convertedAmount
                                )
                            } $receiveCurrency. Commission Fee - ${
                                formatUtils.formatAmountWithOutSign(
                                    commission
                                )
                            } $sellCurrency"
                        emit(Resource.Success(message))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "convertCurrency: $e")
                emit(Resource.Error(apiError.auditError(e)))
            }
        }
    }

    private fun getCommission(amount: Double, commissionPercent: Double): Double {
        return (commissionPercent * amount) / 100
    }
}