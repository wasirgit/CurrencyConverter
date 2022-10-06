package com.wasir.droid.currencyexchange.data.repository

import android.util.Log
import com.wasir.droid.currencyexchange.data.api.CurrencyExchangeService
import com.wasir.droid.currencyexchange.data.database.dao.CurrencyExchangeDao
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.networking.exception.ApiError
import com.wasir.droid.currencyexchange.data.scheduler.AppConfigSync
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
    private val formatUtils: FormatUtils,
    private val appConfigSync: AppConfigSync
) :
    ExchangeRateRepo {
    private val TAG = "ExchangeRateRepoImpl"
    override suspend fun loadConfiguration(): Flow<Resource<String>> = flow {

    }


    override suspend fun calculateReceiverAmount(
        sellAmount: Double,
        base: String,
        symbols: String
    ): Flow<Resource<Double>> =
        flow {
            emit(Resource.Loading())
            try {
                val exchangeRate = getExchangeRate(base, symbols)
                Log.d(TAG, "calculateReceiverAmount:base = $base symbols= $symbols $exchangeRate")
                emit(Resource.Success(sellAmount * exchangeRate))
            } catch (e: Exception) {
                Log.e(TAG, "calculateReceiverAmount: $e")
                emit(Resource.Error(apiError.auditError(e)))
            }
        }

    override suspend fun convertCurrency(
        sellAmount: Double,
        base: String,
        symbol: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        if (sellAmount <= 0) {
            emit(Resource.Error("Amount must be greater then 0"))
        } else {
            try {
                val config = dao.getConfig()

                val exchangeRate = getExchangeRate(base, symbol)
                val sellAccounts = dao.getAccountByCurrency(base)
                val receiveAccount = dao.getAccountByCurrency(symbol)
                val convertedAmount = sellAmount * exchangeRate

                val commission = getCommission(sellAmount, convertedAmount, config);


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
                    dao.updateAccBalanceByCurrencyCode(base, accBalanceAfterSell)
                    dao.updateAccBalanceByCurrencyCode(symbol, accBalanceAfterReceive)
                    val message =
                        generateSuccessMessage(
                            sellAmount,
                            base,
                            convertedAmount,
                            symbol,
                            commission
                        )
                    config.total_convert = config.total_convert + 1
                    dao.updateConfig(config)
                    emit(Resource.Success(message))
                }

            } catch (e: Exception) {
                Log.e(TAG, "convertCurrency: $e")
                emit(Resource.Error(apiError.auditError(e)))
            }
        }
    }

    private fun generateSuccessMessage(
        sellAmount: Double,
        base: String,
        convertedAmount: Double,
        symbol: String,
        commission: Double
    ): String {
        val message =
            "You have converted ${formatUtils.formatAmountWithOutSign(sellAmount)} $base to ${
                formatUtils.formatAmountWithOutSign(
                    convertedAmount
                )
            } $symbol. Commission Fee - ${
                formatUtils.formatAmountWithOutSign(
                    commission
                )
            } $base"
        return message
    }

    private fun getExchangeRate(base: String, symbols: String): Double {
        val currencyExchangeRate = appConfigSync.getCurrencyExchangeRate()
        val rate = currencyExchangeRate?.rates
        val baseCurrency = currencyExchangeRate?.base

        rate?.let { rateMap ->
            baseCurrency?.let { _base ->
                if (_base.equals(base, ignoreCase = true)) {
                    rateMap[symbols]?.let {
                        return it
                    } ?: 0.00.toDouble()
                } else {
                    rateMap[base]?.let { valueBase ->
                        rateMap[symbols]?.let { valueSymbols ->
                            return (1 / valueBase) * valueSymbols
                        }
                    }
                }
            }
        }

        return 0.toDouble()
    }

    private fun getCommission(
        sellAmount: Double,
        convertedAmount: Double,
        config: ConfigEntity
    ): Double {
        if (config.every_nth_conversion_free > 0 && config.total_convert == config.every_nth_conversion_free - 1) {
            return 0.toDouble()
        } else if (config.total_free_conversion <= config.total_convert && sellAmount <= config.max_free_amount) {
            return 0.toDouble()
        } else {
            return (config.commission * convertedAmount) / 100
        }
    }
}